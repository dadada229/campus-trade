package com.example.graduation_project.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduation_project.common.ResultCode;
import com.example.graduation_project.common.UserContext;
import com.example.graduation_project.dto.CreateOrderRequest;
import com.example.graduation_project.entity.Address;
import com.example.graduation_project.entity.Order;
import com.example.graduation_project.entity.Product;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.exception.BusinessException;
import com.example.graduation_project.mapper.AddressMapper;
import com.example.graduation_project.mapper.OrderMapper;
import com.example.graduation_project.mapper.ProductMapper;
import com.example.graduation_project.mapper.UserMapper;
import com.example.graduation_project.service.OrderService;
import com.example.graduation_project.vo.OrderVO;
import com.example.graduation_project.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;
    private final UserMapper userMapper;

    private String getStatusText(Integer status) {
        switch (status) {
            case 1:
                return "待确认";
            case 2:
                return "交易中";
            case 3:
                return "已完成";
            case 4:
                return "已取消";
            default:
                return "未知状态";
        }
    }

    private String getFirstImage(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.get(0);
    }

    private OrderVO buildOrderVO(Order order) {
        log.info("开始构建OrderVO, orderId: {}", order.getId());
        
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusText(getStatusText(order.getStatus()));

        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            vo.setProductId(product.getId());
            vo.setProductTitle(product.getTitle());
            vo.setProductPrice(product.getPrice());
            vo.setProductImage(getFirstImage(product.getImages()));
            
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(product, productVO);
            vo.setProductInfo(productVO);
            log.info("产品信息加载完成, productId: {}", product.getId());
        } else {
            log.warn("订单关联的产品不存在, productId: {}", order.getProductId());
        }

        User buyer = userMapper.selectById(order.getBuyerId());
        if (buyer != null) {
            vo.setBuyerNickname(buyer.getNickname());
            vo.setBuyerAvatar(buyer.getAvatar());
        }

        User seller = userMapper.selectById(order.getSellerId());
        if (seller != null) {
            vo.setSellerNickname(seller.getNickname());
            vo.setSellerAvatar(seller.getAvatar());
        }

        Long currentUserId = UserContext.getUserId();
        vo.setIsBuyer(order.getBuyerId().equals(currentUserId));
        vo.setIsSeller(order.getSellerId().equals(currentUserId));

        log.info("OrderVO构建完成, orderId: {}, isBuyer: {}, isSeller: {}", 
                 order.getId(), vo.getIsBuyer(), vo.getIsSeller());
        return vo;
    }

    private IPage<OrderVO> buildOrderVOPage(IPage<Order> orderPage) {
        IPage<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderVO> voList = orderPage.getRecords().stream().map(this::buildOrderVO).toList();
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public OrderVO createOrder(Long userId, CreateOrderRequest request) {
        log.info("开始创建订单, userId: {}, productId: {}, addressId: {}", userId, request.getProductId(), request.getAddressId());

        // 1. 验证商品
        Product product = productMapper.selectById(request.getProductId());
        if (product == null) {
            log.error("商品不存在, productId: {}", request.getProductId());
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }
        if (product.getStatus() != 1) {
            log.error("商品已售罄, productId: {}, status: {}", request.getProductId(), product.getStatus());
            throw new BusinessException(ResultCode.PRODUCT_SOLD_OUT);
        }
        log.info("商品验证通过, productId: {}", product.getId());

        // 2. 验证地址
        Address address = addressMapper.selectById(request.getAddressId());
        if (address == null) {
            log.error("地址不存在, addressId: {}", request.getAddressId());
            throw new BusinessException(ResultCode.ADDRESS_NOT_EXIST);
        }
        if (!address.getUserId().equals(userId)) {
            log.error("地址不属于当前用户, userId: {}, addressUserId: {}", userId, address.getUserId());
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        log.info("地址验证通过, addressId: {}", address.getId());

        // 3. 使用乐观锁更新商品状态 - 防止超卖
        Product updateProduct = new Product();
        updateProduct.setId(product.getId());
        updateProduct.setStatus(2);
        
        LambdaQueryWrapper<Product> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(Product::getId, product.getId());
        updateWrapper.eq(Product::getStatus, 1);
        
        int updated = productMapper.update(updateProduct, updateWrapper);
        if (updated == 0) {
            log.error("商品状态更新失败，可能已被抢购, productId: {}", product.getId());
            throw new BusinessException(ResultCode.PRODUCT_SOLD_OUT);
        }
        log.info("商品状态更新成功, productId: {}", product.getId());

        // 4. 创建地址快照 - 只保存必要信息，不使用完整实体防止序列化问题
        Map<String, Object> addressSnapshot = new HashMap<>();
        addressSnapshot.put("receiverName", address.getReceiverName());
        addressSnapshot.put("phone", address.getPhone());
        addressSnapshot.put("province", address.getProvince());
        addressSnapshot.put("city", address.getCity());
        addressSnapshot.put("district", address.getDistrict());
        addressSnapshot.put("detail", address.getDetail());

        // 5. 创建订单
        Order order = new Order();
        order.setOrderNo(IdUtil.simpleUUID());
        order.setBuyerId(userId);
        order.setSellerId(product.getSellerId());
        order.setProductId(product.getId());
        order.setAddressSnapshot(addressSnapshot);
        order.setBuyerMessage(request.getBuyerMessage());
        order.setStatus(1);
        
        save(order);
        log.info("订单创建成功, orderId: {}, orderNo: {}", order.getId(), order.getOrderNo());

        // 6. 构建返回对象
        return buildOrderVO(order);
    }

    @Override
    public OrderVO getOrderDetail(Long userId, Long orderId) {
        log.info("获取订单详情, userId: {}, orderId: {}", userId, orderId);
        Order order = getById(orderId);
        if (order == null) {
            log.error("订单不存在, orderId: {}", orderId);
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            log.error("订单无权访问, userId: {}, buyerId: {}, sellerId: {}", userId, order.getBuyerId(), order.getSellerId());
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return buildOrderVO(order);
    }

    @Override
    public IPage<OrderVO> getMyOrders(Long userId, String status, Integer page, Integer size) {
        log.info("获取我的订单, userId: {}, status: {}, page: {}, size: {}", userId, status, page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getBuyerId, userId);
        // 如果有状态参数，则按状态筛选
        if (status != null && !"all".equals(status) && !status.isEmpty()) {
            try {
                wrapper.eq(Order::getStatus, Integer.parseInt(status));
            } catch (NumberFormatException e) {
                log.warn("状态参数格式错误: {}, 忽略状态筛选", status);
            }
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        IPage<Order> orderPage = page(new Page<>(page, size), wrapper);
        return buildOrderVOPage(orderPage);
    }

    @Override
    public IPage<OrderVO> getSoldOrders(Long userId, String status, Integer page, Integer size) {
        log.info("获取售出订单, userId: {}, status: {}, page: {}, size: {}", userId, status, page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getSellerId, userId);
        // 如果有状态参数，则按状态筛选
        if (status != null && !"all".equals(status) && !status.isEmpty()) {
            try {
                wrapper.eq(Order::getStatus, Integer.parseInt(status));
            } catch (NumberFormatException e) {
                log.warn("状态参数格式错误: {}, 忽略状态筛选", status);
            }
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        IPage<Order> orderPage = page(new Page<>(page, size), wrapper);
        return buildOrderVOPage(orderPage);
    }

    @Override
    @Transactional
    public void confirmOrder(Long sellerId, Long orderId) {
        log.info("确认订单, sellerId: {}, orderId: {}", sellerId, orderId);
        Order order = getById(orderId);
        if (order == null) {
            log.error("订单不存在, orderId: {}", orderId);
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
        if (!order.getSellerId().equals(sellerId)) {
            log.error("订单无权操作, sellerId: {}, orderSellerId: {}", sellerId, order.getSellerId());
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        if (order.getStatus() != 1) {
            log.error("订单状态错误, currentStatus: {}", order.getStatus());
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(2);
        updateById(order);
        log.info("订单确认成功, orderId: {}", orderId);
    }

    @Override
    @Transactional
    public void completeOrder(Long userId, Long orderId) {
        log.info("完成订单, userId: {}, orderId: {}", userId, orderId);
        Order order = getById(orderId);
        if (order == null) {
            log.error("订单不存在, orderId: {}", orderId);
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            log.error("订单无权访问, userId: {}, buyerId: {}, sellerId: {}", userId, order.getBuyerId(), order.getSellerId());
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        if (order.getStatus() != 2) {
            log.error("订单状态错误, currentStatus: {}", order.getStatus());
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(3);
        updateById(order);
        log.info("订单完成成功, orderId: {}", orderId);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        log.info("取消订单, userId: {}, orderId: {}", userId, orderId);
        Order order = getById(orderId);
        if (order == null) {
            log.error("订单不存在, orderId: {}", orderId);
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            log.error("订单无权访问, userId: {}, buyerId: {}, sellerId: {}", userId, order.getBuyerId(), order.getSellerId());
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            log.error("订单状态错误, currentStatus: {}", order.getStatus());
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        order.setStatus(4);
        updateById(order);
        log.info("订单状态更新为已取消, orderId: {}", orderId);

        // 恢复商品状态
        Product product = new Product();
        product.setId(order.getProductId());
        product.setStatus(1);
        productMapper.updateById(product);
        log.info("商品状态恢复成功, productId: {}", order.getProductId());
    }

}

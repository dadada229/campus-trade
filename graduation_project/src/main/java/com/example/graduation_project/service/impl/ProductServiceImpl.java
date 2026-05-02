package com.example.graduation_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduation_project.common.ResultCode;
import com.example.graduation_project.common.UserContext;
import com.example.graduation_project.dto.ProductPublishRequest;
import com.example.graduation_project.entity.Favorite;
import com.example.graduation_project.entity.Product;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.exception.BusinessException;
import com.example.graduation_project.mapper.FavoriteMapper;
import com.example.graduation_project.mapper.ProductMapper;
import com.example.graduation_project.mapper.UserMapper;
import com.example.graduation_project.service.ProductService;
import com.example.graduation_project.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final UserMapper userMapper;
    private final FavoriteMapper favoriteMapper;

    @Override
    public Product getProductById(Long productId) {
        return getById(productId);
    }

    private String getConditionText(Integer condition) {
        if (condition == null) return "全新";
        switch (condition) {
            case 1: return "全新";
            case 2: return "几乎全新";
            case 3: return "轻微使用";
            case 4: return "明显痕迹";
            default: return "全新";
        }
    }

    private void fillProductVO(ProductVO vo, Product product, Long userId) {
        vo.setId(product.getId());
        vo.setSellerId(product.getSellerId());
        vo.setTitle(product.getTitle());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setCondition(product.getCondition());
        vo.setConditionText(getConditionText(product.getCondition()));
        vo.setCategoryId(product.getCategoryId());
        vo.setImages(product.getImages());
        vo.setTradePlace(product.getTradePlace());
        vo.setStatus(product.getStatus());
        vo.setViewCount(product.getViewCount());
        vo.setCreatedAt(product.getCreatedAt());

        User seller = userMapper.selectById(product.getSellerId());
        if (seller != null) {
            vo.setSellerNickname(seller.getNickname());
            vo.setSellerName(seller.getNickname());
            vo.setSellerAvatar(seller.getAvatar());
            vo.setSellerSchool(seller.getSchool());
        }

        if (userId != null) {
            LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Favorite::getUserId, userId);
            wrapper.eq(Favorite::getProductId, product.getId());
            Favorite favorite = favoriteMapper.selectOne(wrapper);
            vo.setIsFavorited(favorite != null);
        } else {
            vo.setIsFavorited(false);
        }
    }

    @Override
    public ProductVO publishProduct(Long userId, ProductPublishRequest request) {
        log.info("开始发布商品, userId={}, request={}", userId, request);
        
        if (userId == null) {
            userId = UserContext.getUserId();
        }
        
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setCondition(request.getCondition());
        product.setCategoryId(request.getCategoryId());
        product.setImages(request.getImages());
        product.setTradePlace(request.getTradePlace());
        product.setSellerId(userId);
        product.setStatus(1);
        product.setViewCount(0);
        
        save(product);
        
        log.info("商品发布成功, productId={}", product.getId());
        
        ProductVO vo = new ProductVO();
        fillProductVO(vo, product, userId);
        return vo;
    }

    @Override
    public ProductVO updateProduct(Long userId, Long productId, ProductPublishRequest request) {
        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }
        if (!product.getSellerId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setCondition(request.getCondition());
        product.setCategoryId(request.getCategoryId());
        product.setImages(request.getImages());
        product.setTradePlace(request.getTradePlace());
        
        updateById(product);
        
        ProductVO vo = new ProductVO();
        fillProductVO(vo, product, userId);
        return vo;
    }

    @Override
    public void deleteProduct(Long userId, Long productId) {
        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }
        if (!product.getSellerId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        removeById(productId);
    }

    @Override
    public ProductVO getProductDetail(Long productId, Long userId) {
        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }

        product.setViewCount(product.getViewCount() + 1);
        updateById(product);

        ProductVO vo = new ProductVO();
        fillProductVO(vo, product, userId);
        return vo;
    }

    @Override
    public IPage<ProductVO> getProductList(Long categoryId, String keyword, String sort, Integer page, Integer size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Product::getTitle, keyword)
                    .or()
                    .like(Product::getDescription, keyword));
        }

        if ("price_asc".equals(sort)) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sort)) {
            wrapper.orderByDesc(Product::getPrice);
        } else {
            wrapper.orderByDesc(Product::getCreatedAt);
        }

        IPage<Product> productPage = page(new Page<>(page, size), wrapper);
        IPage<ProductVO> voPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());

        List<ProductVO> voList = productPage.getRecords().stream().map(product -> {
            ProductVO vo = new ProductVO();
            fillProductVO(vo, product, null);
            return vo;
        }).toList();

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public IPage<ProductVO> getMyProducts(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSellerId, userId);
        wrapper.orderByDesc(Product::getCreatedAt);

        IPage<Product> productPage = page(new Page<>(page, size), wrapper);
        IPage<ProductVO> voPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());

        List<ProductVO> voList = productPage.getRecords().stream().map(product -> {
            ProductVO vo = new ProductVO();
            fillProductVO(vo, product, userId);
            return vo;
        }).toList();

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public void offShelfProduct(Long userId, Long productId) {
        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }
        if (!product.getSellerId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        product.setStatus(3);
        updateById(product);
    }

    @Override
    public void markProductSold(Long userId, Long productId) {
        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }
        if (!product.getSellerId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        product.setStatus(2);
        updateById(product);
    }

}

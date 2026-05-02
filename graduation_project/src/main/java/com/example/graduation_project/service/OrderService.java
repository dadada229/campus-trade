package com.example.graduation_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduation_project.dto.CreateOrderRequest;
import com.example.graduation_project.entity.Order;
import com.example.graduation_project.vo.OrderVO;

public interface OrderService extends IService<Order> {

    OrderVO createOrder(Long buyerId, CreateOrderRequest request);

    OrderVO getOrderDetail(Long userId, Long orderId);

    IPage<OrderVO> getMyOrders(Long userId, String status, Integer page, Integer size);

    IPage<OrderVO> getSoldOrders(Long userId, String status, Integer page, Integer size);

    void confirmOrder(Long sellerId, Long orderId);

    void completeOrder(Long userId, Long orderId);

    void cancelOrder(Long userId, Long orderId);

}

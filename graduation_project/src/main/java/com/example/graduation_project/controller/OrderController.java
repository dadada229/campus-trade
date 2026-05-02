package com.example.graduation_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.graduation_project.common.Result;
import com.example.graduation_project.common.ResultGenerator;
import com.example.graduation_project.common.UserContext;
import com.example.graduation_project.dto.CreateOrderRequest;
import com.example.graduation_project.service.OrderService;
import com.example.graduation_project.vo.OrderVO;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public Result<OrderVO> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        Long userId = UserContext.getUserId();
        OrderVO orderVO = orderService.createOrder(userId, createOrderRequest);
        return ResultGenerator.success(orderVO);
    }

    @GetMapping("/orders/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        OrderVO orderVO = orderService.getOrderDetail(userId, id);
        return ResultGenerator.success(orderVO);
    }

    @GetMapping("/user/orders")
    public Result<IPage<OrderVO>> getMyOrders(
            @RequestParam(defaultValue = "buyer") String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = UserContext.getUserId();
        if ("seller".equals(role)) {
            IPage<OrderVO> soldOrders = orderService.getSoldOrders(userId, status, page, size);
            return ResultGenerator.success(soldOrders);
        } else {
            IPage<OrderVO> myOrders = orderService.getMyOrders(userId, status, page, size);
            return ResultGenerator.success(myOrders);
        }
    }

    @PutMapping("/orders/{id}/confirm")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        orderService.confirmOrder(userId, id);
        return ResultGenerator.success();
    }

    @PutMapping("/orders/{id}/complete")
    public Result<Void> completeOrder(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        orderService.completeOrder(userId, id);
        return ResultGenerator.success();
    }

    @PutMapping("/orders/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        orderService.cancelOrder(userId, id);
        return ResultGenerator.success();
    }

}

package com.example.graduation_project.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "地址ID不能为空")
    private Long addressId;

    private String buyerMessage;

}

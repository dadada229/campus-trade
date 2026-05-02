package com.example.graduation_project.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO {

    private Long id;
    private String orderNo;
    private Long buyerId;
    private String buyerNickname;
    private String buyerAvatar;
    private Long sellerId;
    private String sellerNickname;
    private String sellerAvatar;
    private Long productId;
    private String productImage;
    private String productTitle;
    private BigDecimal productPrice;
    private ProductVO productInfo;
    private Object addressSnapshot;
    private String buyerMessage;
    private Integer status;
    private String statusText;
    private LocalDateTime createdAt;
    private Boolean isBuyer;
    private Boolean isSeller;

}

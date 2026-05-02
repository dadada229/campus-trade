package com.example.graduation_project.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {

    private Long id;

    private Long sellerId;

    private String sellerNickname;
    private String sellerName;

    private String sellerAvatar;

    private String sellerSchool;

    private String title;

    private String description;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer condition;
    private String conditionText;

    private Integer categoryId;

    private List<String> images;

    private String tradePlace;

    private Integer status;

    private Integer viewCount;

    private Boolean isFavorited;

    private LocalDateTime createdAt;

}

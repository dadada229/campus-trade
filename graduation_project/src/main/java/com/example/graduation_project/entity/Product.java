package com.example.graduation_project.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.example.graduation_project.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "product", autoResultMap = true)
public class Product extends BaseEntity {

    @TableField("seller_id")
    private Long sellerId;

    private String title;

    private String description;

    private BigDecimal price;

    private BigDecimal originalPrice;

    @TableField("`condition`")
    private Integer condition;

    private Integer categoryId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    @TableField("trade_place")
    private String tradePlace;

    private Integer status;

    private Integer viewCount;

}

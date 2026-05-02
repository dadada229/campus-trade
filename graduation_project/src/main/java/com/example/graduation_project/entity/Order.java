package com.example.graduation_project.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.example.graduation_project.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "orders", autoResultMap = true)
public class Order extends BaseEntity {

    private String orderNo;

    private Long buyerId;

    private Long sellerId;

    private Long productId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object addressSnapshot;

    private String buyerMessage;

    private Integer status;

}

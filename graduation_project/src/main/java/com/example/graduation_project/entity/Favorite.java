package com.example.graduation_project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.graduation_project.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("favorite")
public class Favorite extends BaseEntity {

    private Long userId;

    private Long productId;

}

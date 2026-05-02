package com.example.graduation_project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.graduation_project.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity {

    private String name;

    private Integer parentId;

    private Integer sort;

}

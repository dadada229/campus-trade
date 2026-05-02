package com.example.graduation_project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.graduation_project.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("banner")
public class Banner extends BaseEntity {

    private String imageUrl;

    private String linkUrl;

    private Integer sort;

    private Integer isEnabled;

}

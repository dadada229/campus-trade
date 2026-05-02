package com.example.graduation_project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.graduation_project.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    private String phone;

    private String password;

    private String nickname;

    private String avatar;

    private String realName;

    private String school;

    private String department;

    private Integer role;

    private Integer status;

    private String email;

}

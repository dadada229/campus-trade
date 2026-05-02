package com.example.graduation_project.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private Long id;

    private String phone;

    private String nickname;

    private String avatar;

    private String realName;

    private String school;

    private String department;

    private Integer role;

}

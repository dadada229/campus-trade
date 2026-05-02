package com.example.graduation_project.controller;

import com.example.graduation_project.common.Result;
import com.example.graduation_project.common.ResultGenerator;
import com.example.graduation_project.common.UserContext;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.service.UserService;
import com.example.graduation_project.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        Long userId = UserContext.getUserId();
        User user = userService.getUserById(userId);
        UserVO userVO = new UserVO(
                user.getId(), user.getPhone(), user.getNickname(), user.getAvatar(),
                user.getRealName(), user.getSchool(), user.getDepartment(), user.getRole()
        );
        return ResultGenerator.success(userVO);
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User user) {
        Long userId = UserContext.getUserId();
        userService.updateUser(userId, user);
        return ResultGenerator.success();
    }

}

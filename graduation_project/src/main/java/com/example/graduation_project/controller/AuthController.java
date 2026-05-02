package com.example.graduation_project.controller;

import com.example.graduation_project.common.Result;
import com.example.graduation_project.common.ResultGenerator;
import com.example.graduation_project.dto.LoginRequest;
import com.example.graduation_project.dto.RegisterRequest;
import com.example.graduation_project.service.UserService;
import com.example.graduation_project.vo.LoginResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ResultGenerator.success();
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResultGenerator.success(response);
    }

}

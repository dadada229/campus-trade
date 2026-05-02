package com.example.graduation_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduation_project.dto.LoginRequest;
import com.example.graduation_project.dto.RegisterRequest;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.vo.LoginResponse;

import java.util.List;

public interface UserService extends IService<User> {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    User getUserById(Long userId);

    void updateUser(Long userId, User user);

    List<User> getAllUsers(String keyword, String role);

    void addUser(User user);

    void adminUpdateUser(Long id, User user);

    void deleteUser(Long id);

    void toggleUser(Long id);

}

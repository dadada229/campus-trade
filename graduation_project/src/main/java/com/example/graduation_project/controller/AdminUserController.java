package com.example.graduation_project.controller;

import com.example.graduation_project.common.Result;
import com.example.graduation_project.common.ResultGenerator;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public Result<List<Map<String, Object>>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status
    ) {
        List<User> users = userService.getAllUsers(keyword, role);
        List<Map<String, Object>> result = users.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("nickname", user.getNickname());
            map.put("username", user.getPhone());
            map.put("phone", user.getPhone());
            map.put("email", user.getEmail());
            map.put("avatar", user.getAvatar());
            map.put("role", user.getRole() == 1 ? "admin" : "user");
            map.put("status", user.getStatus() == null || user.getStatus() == 1);
            map.put("createdAt", user.getCreatedAt());
            return map;
        }).collect(Collectors.toList());
        return ResultGenerator.success(result);
    }

    @PostMapping
    public Result<Void> addUser(@RequestBody Map<String, Object> data) {
        User user = new User();
        user.setPhone((String) data.get("username"));
        user.setPassword((String) data.get("password"));
        user.setNickname((String) data.get("nickname"));
        user.setEmail((String) data.get("email"));
        user.setPhone((String) data.get("phone"));
        user.setRole("admin".equals(data.get("role")) ? 1 : 0);
        userService.addUser(user);
        return ResultGenerator.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        User user = new User();
        user.setNickname((String) data.get("nickname"));
        user.setEmail((String) data.get("email"));
        user.setPhone((String) data.get("phone"));
        user.setRole("admin".equals(data.get("role")) ? 1 : 0);
        user.setStatus((Boolean) data.get("status") ? 1 : 0);
        if (data.containsKey("password")) {
            user.setPassword((String) data.get("password"));
        }
        userService.adminUpdateUser(id, user);
        return ResultGenerator.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResultGenerator.success();
    }

    @PutMapping("/{id}/toggle")
    public Result<Void> toggleUser(@PathVariable Long id) {
        userService.toggleUser(id);
        return ResultGenerator.success();
    }

}

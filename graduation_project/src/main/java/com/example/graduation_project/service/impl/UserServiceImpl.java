package com.example.graduation_project.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduation_project.common.ResultCode;
import com.example.graduation_project.dto.LoginRequest;
import com.example.graduation_project.dto.RegisterRequest;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.exception.BusinessException;
import com.example.graduation_project.mapper.UserMapper;
import com.example.graduation_project.service.UserService;
import com.example.graduation_project.utils.JwtUtil;
import com.example.graduation_project.vo.LoginResponse;
import com.example.graduation_project.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, request.getPhone());
        User existUser = getOne(wrapper);
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXIST);
        }

        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(BCrypt.hashpw(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setRealName(request.getRealName());
        user.setSchool(request.getSchool());
        user.setDepartment(request.getDepartment());
        user.setRole(0);
        user.setStatus(1);
        save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, request.getPhone());
        User user = getOne(wrapper);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        UserVO userVO = new UserVO(
                user.getId(), user.getPhone(), user.getNickname(), user.getAvatar(),
                user.getRealName(), user.getSchool(), user.getDepartment(), user.getRole()
        );
        return new LoginResponse(token, userVO);
    }

    @Override
    public User getUserById(Long userId) {
        return getById(userId);
    }

    @Override
    public void updateUser(Long userId, User user) {
        user.setId(userId);
        updateById(user);
    }

    @Override
    public List<User> getAllUsers(String keyword, String role) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getPhone, keyword)
                    .or()
                    .like(User::getEmail, keyword)
            );
        }
        if (StrUtil.isNotBlank(role)) {
            wrapper.eq(User::getRole, "admin".equals(role) ? 1 : 0);
        }
        wrapper.orderByDesc(User::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public void addUser(User user) {
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        user.setStatus(1);
        save(user);
    }

    @Override
    public void adminUpdateUser(Long id, User user) {
        user.setId(id);
        if (StrUtil.isBlank(user.getPassword())) {
            user.setPassword(null);
        } else {
            user.setPassword(BCrypt.hashpw(user.getPassword()));
        }
        updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        removeById(id);
    }

    @Override
    public void toggleUser(Long id) {
        User user = getById(id);
        if (user != null) {
            user.setStatus(user.getStatus() == null || user.getStatus() == 1 ? 0 : 1);
            updateById(user);
        }
    }

}

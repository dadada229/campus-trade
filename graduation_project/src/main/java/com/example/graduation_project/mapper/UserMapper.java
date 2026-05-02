package com.example.graduation_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.graduation_project.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}

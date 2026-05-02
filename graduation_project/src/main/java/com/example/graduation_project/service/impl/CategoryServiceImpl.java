package com.example.graduation_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduation_project.entity.Category;
import com.example.graduation_project.mapper.CategoryMapper;
import com.example.graduation_project.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getCategoryList() {
        return list();
    }

    @Override
    public List<Category> getCategoryTree() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        return list(wrapper);
    }

    @Override
    public void addCategory(Category category) {
        save(category);
    }

    @Override
    public void updateCategory(Long id, Category category) {
        category.setId(id);
        updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        removeById(id);
    }

}

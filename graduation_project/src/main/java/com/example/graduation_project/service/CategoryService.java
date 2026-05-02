package com.example.graduation_project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduation_project.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<Category> getCategoryList();

    List<Category> getCategoryTree();

    void addCategory(Category category);

    void updateCategory(Long id, Category category);

    void deleteCategory(Long id);

}

package com.example.graduation_project.controller;

import com.example.graduation_project.common.Result;
import com.example.graduation_project.common.ResultGenerator;
import com.example.graduation_project.entity.Category;
import com.example.graduation_project.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Result<List<Category>> getCategoryList() {
        return ResultGenerator.success(categoryService.getCategoryList());
    }

    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        return ResultGenerator.success(categoryService.getCategoryTree());
    }

}

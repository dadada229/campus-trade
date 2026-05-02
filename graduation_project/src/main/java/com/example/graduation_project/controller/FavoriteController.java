package com.example.graduation_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.graduation_project.common.Result;
import com.example.graduation_project.common.ResultGenerator;
import com.example.graduation_project.common.UserContext;
import com.example.graduation_project.service.FavoriteService;
import com.example.graduation_project.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/products/{id}/favorite")
    public Result<Void> addFavorite(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        favoriteService.addFavorite(userId, id);
        return ResultGenerator.success();
    }

    @PostMapping("/favorites/{productId}")
    public Result<Void> addFavoriteByProductId(@PathVariable Long productId) {
        Long userId = UserContext.getUserId();
        favoriteService.addFavorite(userId, productId);
        return ResultGenerator.success();
    }

    @DeleteMapping("/products/{id}/favorite")
    public Result<Void> removeFavorite(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        favoriteService.removeFavorite(userId, id);
        return ResultGenerator.success();
    }

    @DeleteMapping("/favorites/{productId}")
    public Result<Void> removeFavoriteByProductId(@PathVariable Long productId) {
        Long userId = UserContext.getUserId();
        favoriteService.removeFavorite(userId, productId);
        return ResultGenerator.success();
    }

    @GetMapping("/user/favorites")
    public Result<IPage<ProductVO>> getMyFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = UserContext.getUserId();
        IPage<ProductVO> myFavorites = favoriteService.getMyFavorites(userId, page, size);
        return ResultGenerator.success(myFavorites);
    }

    @GetMapping("/favorites")
    public Result<IPage<ProductVO>> getFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = UserContext.getUserId();
        IPage<ProductVO> myFavorites = favoriteService.getMyFavorites(userId, page, size);
        return ResultGenerator.success(myFavorites);
    }

}

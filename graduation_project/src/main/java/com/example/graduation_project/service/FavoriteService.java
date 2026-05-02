package com.example.graduation_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduation_project.entity.Favorite;
import com.example.graduation_project.vo.ProductVO;

public interface FavoriteService extends IService<Favorite> {

    void addFavorite(Long userId, Long productId);

    void removeFavorite(Long userId, Long productId);

    IPage<ProductVO> getMyFavorites(Long userId, Integer page, Integer size);

}

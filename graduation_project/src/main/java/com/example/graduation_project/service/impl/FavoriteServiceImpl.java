package com.example.graduation_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduation_project.entity.Favorite;
import com.example.graduation_project.entity.Product;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.mapper.FavoriteMapper;
import com.example.graduation_project.mapper.ProductMapper;
import com.example.graduation_project.mapper.UserMapper;
import com.example.graduation_project.service.FavoriteService;
import com.example.graduation_project.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    private String getConditionText(Integer condition) {
        if (condition == null) return "全新";
        switch (condition) {
            case 1: return "全新";
            case 2: return "几乎全新";
            case 3: return "轻微使用";
            case 4: return "明显痕迹";
            default: return "全新";
        }
    }

    private void fillProductVO(ProductVO vo, Product product, Long userId) {
        vo.setId(product.getId());
        vo.setSellerId(product.getSellerId());
        vo.setTitle(product.getTitle());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setCondition(product.getCondition());
        vo.setConditionText(getConditionText(product.getCondition()));
        vo.setCategoryId(product.getCategoryId());
        vo.setImages(product.getImages());
        vo.setTradePlace(product.getTradePlace());
        vo.setStatus(product.getStatus());
        vo.setViewCount(product.getViewCount());
        vo.setCreatedAt(product.getCreatedAt());

        User seller = userMapper.selectById(product.getSellerId());
        if (seller != null) {
            vo.setSellerNickname(seller.getNickname());
            vo.setSellerName(seller.getNickname());
            vo.setSellerAvatar(seller.getAvatar());
            vo.setSellerSchool(seller.getSchool());
        }

        vo.setIsFavorited(true);
    }

    @Override
    public void addFavorite(Long userId, Long productId) {
        log.info("添加收藏: userId={}, productId={}", userId, productId);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId);
        Favorite exist = getOne(wrapper);
        if (exist == null) {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setProductId(productId);
            save(favorite);
        }
    }

    @Override
    public void removeFavorite(Long userId, Long productId) {
        log.info("移除收藏: userId={}, productId={}", userId, productId);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId);
        remove(wrapper);
    }

    @Override
    public IPage<ProductVO> getMyFavorites(Long userId, Integer page, Integer size) {
        log.info("获取收藏列表: userId={}", userId);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreatedAt);
        IPage<Favorite> favoritePage = page(new Page<>(page, size), wrapper);

        IPage<ProductVO> voPage = new Page<>(favoritePage.getCurrent(), favoritePage.getSize(), favoritePage.getTotal());

        List<ProductVO> voList = favoritePage.getRecords().stream().map(favorite -> {
            Product product = productMapper.selectById(favorite.getProductId());
            if (product == null) return null;
            ProductVO vo = new ProductVO();
            fillProductVO(vo, product, userId);
            return vo;
        }).filter(vo -> vo != null).toList();

        voPage.setRecords(voList);
        return voPage;
    }

}

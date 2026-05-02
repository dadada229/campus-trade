package com.example.graduation_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduation_project.entity.Banner;
import com.example.graduation_project.mapper.BannerMapper;
import com.example.graduation_project.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public List<Banner> getEnabledBanners() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getIsEnabled, 1)
                .orderByAsc(Banner::getSort);
        return list(wrapper);
    }

    @Override
    public List<Banner> getAllBanners() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Banner::getSort);
        return list(wrapper);
    }

    @Override
    public void addBanner(Banner banner) {
        save(banner);
    }

    @Override
    public void updateBanner(Long id, Banner banner) {
        banner.setId(id);
        updateById(banner);
    }

    @Override
    public void deleteBanner(Long id) {
        removeById(id);
    }

    @Override
    public void toggleBanner(Long id) {
        Banner banner = getById(id);
        if (banner != null) {
            banner.setIsEnabled(banner.getIsEnabled() == 1 ? 0 : 1);
            updateById(banner);
        }
    }

}

package com.example.graduation_project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduation_project.entity.Banner;

import java.util.List;

public interface BannerService extends IService<Banner> {

    List<Banner> getEnabledBanners();

    List<Banner> getAllBanners();

    void addBanner(Banner banner);

    void updateBanner(Long id, Banner banner);

    void deleteBanner(Long id);

    void toggleBanner(Long id);

}

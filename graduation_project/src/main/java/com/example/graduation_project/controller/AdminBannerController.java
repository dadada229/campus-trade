package com.example.graduation_project.controller;

import com.example.graduation_project.common.Result;
import com.example.graduation_project.common.ResultGenerator;
import com.example.graduation_project.entity.Banner;
import com.example.graduation_project.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;

    @GetMapping
    public Result<List<Map<String, Object>>> getBanners() {
        List<Banner> banners = bannerService.getAllBanners();
        List<Map<String, Object>> result = banners.stream().map(banner -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", banner.getId());
            map.put("image", banner.getImageUrl());
            map.put("link", banner.getLinkUrl());
            map.put("sort", banner.getSort());
            map.put("enabled", banner.getIsEnabled() == 1);
            return map;
        }).collect(Collectors.toList());
        return ResultGenerator.success(result);
    }

    @PostMapping
    public Result<Void> addBanner(@RequestBody Map<String, Object> data) {
        Banner banner = new Banner();
        banner.setImageUrl((String) data.get("image"));
        banner.setLinkUrl((String) data.get("link"));
        banner.setSort((Integer) data.get("sort"));
        banner.setIsEnabled((Boolean) data.get("enabled") ? 1 : 0);
        bannerService.addBanner(banner);
        return ResultGenerator.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateBanner(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        Banner banner = new Banner();
        banner.setImageUrl((String) data.get("image"));
        banner.setLinkUrl((String) data.get("link"));
        banner.setSort((Integer) data.get("sort"));
        banner.setIsEnabled((Boolean) data.get("enabled") ? 1 : 0);
        bannerService.updateBanner(id, banner);
        return ResultGenerator.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResultGenerator.success();
    }

    @PutMapping("/{id}/toggle")
    public Result<Void> toggleBanner(@PathVariable Long id) {
        bannerService.toggleBanner(id);
        return ResultGenerator.success();
    }

}

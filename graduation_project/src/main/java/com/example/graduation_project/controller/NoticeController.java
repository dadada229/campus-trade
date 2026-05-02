package com.example.graduation_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.graduation_project.common.Result;
import com.example.graduation_project.common.ResultGenerator;
import com.example.graduation_project.entity.Notice;
import com.example.graduation_project.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public Result<IPage<Notice>> getNoticeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Integer pinned) {
        IPage<Notice> noticeList = noticeService.getNoticeList(page, size, pinned);
        return ResultGenerator.success(noticeList);
    }

    @GetMapping("/{id}")
    public Result<Notice> getNoticeDetail(@PathVariable Long id) {
        Notice notice = noticeService.getNoticeDetail(id);
        return ResultGenerator.success(notice);
    }

}

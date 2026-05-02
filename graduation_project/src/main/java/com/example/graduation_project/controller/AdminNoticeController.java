package com.example.graduation_project.controller;

import com.example.graduation_project.common.Result;
import com.example.graduation_project.common.ResultGenerator;
import com.example.graduation_project.entity.Notice;
import com.example.graduation_project.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public Result<List<Map<String, Object>>> getNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        List<Map<String, Object>> result = notices.stream().map(notice -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", notice.getId());
            map.put("title", notice.getTitle());
            map.put("content", notice.getContent());
            map.put("isPinned", notice.getIsPinned() == 1);
            map.put("createdAt", notice.getCreatedAt());
            return map;
        }).collect(Collectors.toList());
        return ResultGenerator.success(result);
    }

    @PostMapping
    public Result<Void> addNotice(@RequestBody Map<String, Object> data) {
        Notice notice = new Notice();
        notice.setTitle((String) data.get("title"));
        notice.setContent((String) data.get("content"));
        notice.setIsPinned((Boolean) data.get("isPinned") ? 1 : 0);
        noticeService.addNotice(notice);
        return ResultGenerator.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateNotice(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        Notice notice = new Notice();
        notice.setTitle((String) data.get("title"));
        notice.setContent((String) data.get("content"));
        notice.setIsPinned((Boolean) data.get("isPinned") ? 1 : 0);
        noticeService.updateNotice(id, notice);
        return ResultGenerator.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResultGenerator.success();
    }

    @PutMapping("/{id}/pin")
    public Result<Void> toggleNoticePin(@PathVariable Long id) {
        noticeService.toggleNoticePin(id);
        return ResultGenerator.success();
    }

}

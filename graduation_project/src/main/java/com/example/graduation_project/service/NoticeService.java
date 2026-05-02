package com.example.graduation_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduation_project.entity.Notice;

import java.util.List;

public interface NoticeService extends IService<Notice> {

    IPage<Notice> getNoticeList(Integer page, Integer size, Integer pinned);

    Notice getNoticeDetail(Long id);

    List<Notice> getAllNotices();

    void addNotice(Notice notice);

    void updateNotice(Long id, Notice notice);

    void deleteNotice(Long id);

    void toggleNoticePin(Long id);

}

package com.example.graduation_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduation_project.common.ResultCode;
import com.example.graduation_project.entity.Notice;
import com.example.graduation_project.exception.BusinessException;
import com.example.graduation_project.mapper.NoticeMapper;
import com.example.graduation_project.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public IPage<Notice> getNoticeList(Integer page, Integer size, Integer pinned) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        if (pinned != null) {
            wrapper.eq(Notice::getIsPinned, pinned);
        }
        wrapper.orderByDesc(Notice::getIsPinned, Notice::getCreatedAt);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public Notice getNoticeDetail(Long id) {
        Notice notice = getById(id);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return notice;
    }

    @Override
    public List<Notice> getAllNotices() {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Notice::getIsPinned, Notice::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public void addNotice(Notice notice) {
        save(notice);
    }

    @Override
    public void updateNotice(Long id, Notice notice) {
        notice.setId(id);
        updateById(notice);
    }

    @Override
    public void deleteNotice(Long id) {
        removeById(id);
    }

    @Override
    public void toggleNoticePin(Long id) {
        Notice notice = getById(id);
        if (notice != null) {
            notice.setIsPinned(notice.getIsPinned() == 1 ? 0 : 1);
            updateById(notice);
        }
    }

}

package com.example.graduation_project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.graduation_project.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notice")
public class Notice extends BaseEntity {

    private String title;

    private String content;

    private Integer isPinned;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}

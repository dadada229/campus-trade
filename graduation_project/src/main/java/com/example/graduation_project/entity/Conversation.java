package com.example.graduation_project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.graduation_project.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("conversation")
public class Conversation extends BaseEntity {

    private Long user1Id;

    private Long user2Id;

    private Long productId;

    private Integer user1Unread;

    private Integer user2Unread;

    private String lastMessage;

    private LocalDateTime lastMessageAt;

}

package com.example.graduation_project.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConversationVO {

    private Long id;

    private Long user1Id;

    private Long user2Id;

    private Long productId;

    private String user2Nickname;

    private String user2Name;

    private String user2Avatar;

    private String productTitle;

    private String productImage;

    private String lastMessage;

    private LocalDateTime lastMessageAt;

    private Integer unreadCount;

    private LocalDateTime createdAt;

}

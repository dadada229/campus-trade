package com.example.graduation_project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduation_project.dto.CreateConversationRequest;
import com.example.graduation_project.entity.Conversation;
import com.example.graduation_project.vo.ConversationVO;

import java.util.List;

public interface ConversationService extends IService<Conversation> {

    List<ConversationVO> getConversations(Long userId);

    ConversationVO createOrGetConversation(Long userId, CreateConversationRequest request);

    void markAsRead(Long userId, Long conversationId);

    Conversation getConversationById(Long id);

}

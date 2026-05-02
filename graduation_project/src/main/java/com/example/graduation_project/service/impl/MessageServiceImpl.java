package com.example.graduation_project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduation_project.common.ResultCode;
import com.example.graduation_project.config.ChatWebSocketHandler;
import com.example.graduation_project.dto.WebSocketMessage;
import com.example.graduation_project.entity.Conversation;
import com.example.graduation_project.entity.Message;
import com.example.graduation_project.exception.BusinessException;
import com.example.graduation_project.mapper.MessageMapper;
import com.example.graduation_project.service.ConversationService;
import com.example.graduation_project.service.MessageService;
import com.example.graduation_project.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final ConversationService conversationService;
    private final ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public List<MessageVO> getMessagesByConversationId(Long conversationId) {
        log.info("Getting messages for conversation {}", conversationId);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getConversationId, conversationId);
        wrapper.orderByAsc(Message::getCreatedAt);
        List<Message> messages = list(wrapper);
        
        List<MessageVO> result = new ArrayList<>();
        for (Message message : messages) {
            MessageVO vo = new MessageVO();
            BeanUtil.copyProperties(message, vo);
            result.add(vo);
        }
        log.info("Found {} messages for conversation {}", result.size(), conversationId);
        return result;
    }

    @Override
    @Transactional
    public MessageVO sendMessage(Long senderId, Long conversationId, String content) {
        log.info("User {} is sending message to conversation {}: {}", senderId, conversationId, content);
        Conversation conversation = conversationService.getConversationById(conversationId);
        if (conversation == null) {
            log.error("Conversation not found: {}", conversationId);
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setContent(content);
        save(message);
        log.info("Message saved with ID: {}", message.getId());
        
        conversation.setLastMessage(content);
        conversation.setLastMessageAt(LocalDateTime.now());
        if (senderId.equals(conversation.getUser1Id())) {
            conversation.setUser2Unread(conversation.getUser2Unread() == null ? 1 : conversation.getUser2Unread() + 1);
        } else {
            conversation.setUser1Unread(conversation.getUser1Unread() == null ? 1 : conversation.getUser1Unread() + 1);
        }
        conversationService.updateById(conversation);
        log.info("Conversation updated");
        
        MessageVO vo = new MessageVO();
        BeanUtil.copyProperties(message, vo);
        
        Long receiverId = senderId.equals(conversation.getUser1Id()) 
                ? conversation.getUser2Id() 
                : conversation.getUser1Id();
        log.info("Pushing message to sender {} and receiver {}", senderId, receiverId);
        
        WebSocketMessage wsMessage = new WebSocketMessage();
        wsMessage.setType("new_message");
        wsMessage.setData(vo);
        
        chatWebSocketHandler.sendMessageToUser(senderId, wsMessage);
        chatWebSocketHandler.sendMessageToUser(receiverId, wsMessage);
        log.info("Message pushed successfully");
        
        return vo;
    }
}

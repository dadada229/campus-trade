package com.example.graduation_project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduation_project.entity.Message;
import com.example.graduation_project.vo.MessageVO;

import java.util.List;

public interface MessageService extends IService<Message> {

    List<MessageVO> getMessagesByConversationId(Long conversationId);

    MessageVO sendMessage(Long senderId, Long conversationId, String content);

}

package com.example.graduation_project.controller;

import com.example.graduation_project.common.Result;
import com.example.graduation_project.common.ResultGenerator;
import com.example.graduation_project.common.UserContext;
import com.example.graduation_project.dto.CreateConversationRequest;
import com.example.graduation_project.service.ConversationService;
import com.example.graduation_project.service.MessageService;
import com.example.graduation_project.vo.ConversationVO;
import com.example.graduation_project.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Slf4j
public class ConversationController {

    private final ConversationService conversationService;
    private final MessageService messageService;

    @GetMapping
    public Result<List<ConversationVO>> getConversations() {
        Long userId = UserContext.getUserId();
        log.info("User {} is getting conversations", userId);
        List<ConversationVO> conversations = conversationService.getConversations(userId);
        return ResultGenerator.success(conversations);
    }

    @PostMapping
    public Result<ConversationVO> createOrGetConversation(@RequestBody CreateConversationRequest request) {
        Long userId = UserContext.getUserId();
        log.info("User {} is creating conversation", userId);
        ConversationVO vo = conversationService.createOrGetConversation(userId, request);
        return ResultGenerator.success(vo);
    }

    @GetMapping("/{id}/messages")
    public Result<List<MessageVO>> getMessages(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        log.info("User {} is getting messages for conversation {}", userId, id);
        List<MessageVO> messages = messageService.getMessagesByConversationId(id);
        return ResultGenerator.success(messages);
    }

    @PostMapping("/{id}/messages")
    public Result<MessageVO> sendMessage(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Long userId = UserContext.getUserId();
        String content = request.get("content");
        log.info("User {} is sending message to conversation {}", userId, id);
        MessageVO vo = messageService.sendMessage(userId, id, content);
        return ResultGenerator.success(vo);
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        log.info("User {} is marking conversation {} as read", userId, id);
        conversationService.markAsRead(userId, id);
        return ResultGenerator.success();
    }
}

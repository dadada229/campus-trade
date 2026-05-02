package com.example.graduation_project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduation_project.common.ResultCode;
import com.example.graduation_project.dto.CreateConversationRequest;
import com.example.graduation_project.entity.Conversation;
import com.example.graduation_project.entity.Product;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.exception.BusinessException;
import com.example.graduation_project.mapper.ConversationMapper;
import com.example.graduation_project.service.ConversationService;
import com.example.graduation_project.service.ProductService;
import com.example.graduation_project.service.UserService;
import com.example.graduation_project.vo.ConversationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements ConversationService {

    private final UserService userService;
    private final ProductService productService;

    @Override
    public List<ConversationVO> getConversations(Long userId) {
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Conversation::getUser1Id, userId).or().eq(Conversation::getUser2Id, userId));
        wrapper.orderByDesc(Conversation::getLastMessageAt);
        List<Conversation> conversations = list(wrapper);
        
        List<ConversationVO> result = new ArrayList<>();
        for (Conversation conversation : conversations) {
            ConversationVO vo = convertToVO(conversation, userId);
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional
    public ConversationVO createOrGetConversation(Long userId, CreateConversationRequest request) {
        Long user1Id = Math.min(userId, request.getSellerId());
        Long user2Id = Math.max(userId, request.getSellerId());
        
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getUser1Id, user1Id)
               .eq(Conversation::getUser2Id, user2Id)
               .eq(Conversation::getProductId, request.getProductId());
        Conversation exist = getOne(wrapper);
        
        if (exist != null) {
            return convertToVO(exist, userId);
        }
        
        Conversation conversation = new Conversation();
        conversation.setUser1Id(user1Id);
        conversation.setUser2Id(user2Id);
        conversation.setProductId(request.getProductId());
        conversation.setUser1Unread(0);
        conversation.setUser2Unread(0);
        conversation.setLastMessage(null);
        conversation.setLastMessageAt(null);
        save(conversation);
        
        return convertToVO(conversation, userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long conversationId) {
        Conversation conversation = getById(conversationId);
        if (conversation == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        
        if (userId.equals(conversation.getUser1Id())) {
            conversation.setUser1Unread(0);
        } else if (userId.equals(conversation.getUser2Id())) {
            conversation.setUser2Unread(0);
        }
        updateById(conversation);
    }

    @Override
    public Conversation getConversationById(Long id) {
        return getById(id);
    }

    private ConversationVO convertToVO(Conversation conversation, Long currentUserId) {
        ConversationVO vo = new ConversationVO();
        BeanUtil.copyProperties(conversation, vo);

        Long otherUserId = currentUserId.equals(conversation.getUser1Id()) 
                ? conversation.getUser2Id() 
                : conversation.getUser1Id();
        User otherUser = userService.getUserById(otherUserId);
        if (otherUser != null) {
            vo.setUser2Nickname(otherUser.getNickname());
            vo.setUser2Name(otherUser.getNickname());
            vo.setUser2Avatar(otherUser.getAvatar());
        }

        Product product = productService.getProductById(conversation.getProductId());
        if (product != null) {
            vo.setProductTitle(product.getTitle());
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                vo.setProductImage(product.getImages().get(0));
            }
        }

        if (currentUserId.equals(conversation.getUser1Id())) {
            vo.setUnreadCount(conversation.getUser1Unread());
        } else {
            vo.setUnreadCount(conversation.getUser2Unread());
        }

        return vo;
    }
}

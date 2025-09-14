package com.example.auth.controller;

import com.example.auth.dto.request.CreateSessionRequest;
import com.example.auth.service.ChatService;
import com.example.auth.dto.response.ApiResponse;
import com.example.auth.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.example.auth.repository.UserRepository;
import com.example.auth.dto.response.MessageResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.Map;
import com.example.auth.entity.User;
import java.util.Optional;


@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    private final UserRepository userRepository;
    public ChatController(ChatService chatService, UserRepository userRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Tạo phiên chat với người dùng khác")
    @PostMapping("/create-session")
    public ApiResponse<String> createSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateSessionRequest request) {
        Long fromUserId = getUserIdFromUserDetails(userDetails);
        String result = chatService.createChatSession(fromUserId, request.getToUserId(), request.getContent());
        return new ApiResponse<>(200, result, null);
    }

    @Operation(summary = "Gửi tin nhắn đến người dùng khác")
    @PostMapping("/send")
    public ApiResponse<MessageResponse> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateSessionRequest request) {

        Long senderId = getUserIdFromUserDetails(userDetails);
        Long toUserId = request.getToUserId();
        String content = request.getContent();

        var fromUser = userRepository.findById(senderId).orElseThrow();
        var toUser = userRepository.findById(toUserId).orElseThrow();
        var session = chatService.getChatSessionRepo().findByUser1AndUser2(fromUser, toUser);
        if (session.isEmpty()) {
            session = chatService.getChatSessionRepo().findByUser1AndUser2(toUser, fromUser);
        }
        if (session.isEmpty()) {
            chatService.createChatSession(senderId, toUserId, content);
        }
        // Fix: Mark messages from receiver to sender as seen
        chatService.markMessagesAsSeen(toUserId, senderId);
        Message msg = chatService.sendMessage(senderId, toUserId, content);
        MessageResponse msgRes = new MessageResponse(msg);
        return new ApiResponse<>(200, "Message sent", msgRes);
    }

    @Operation(summary = "Lấy danh sách các phiên chat của người đăng nhập")
    @GetMapping("/conversations")
    public ApiResponse<List<Map<String, Object>>> getConversations(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        return new ApiResponse<>(200, "OK", chatService.getConversations(userId));
    }

    // Helper method to extract userId from UserDetails
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        // Find user by username, not userId
        return userRepository.findByUsername(userDetails.getUsername())
                .map(u -> u.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
package com.example.auth.ws;

import com.example.auth.entity.Message;
import com.example.auth.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {
    private final ChatService chatService;
    public ChatWebSocketController(ChatService chatService) { this.chatService = chatService; }

    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public Message send(Message message) {
        // Save message and return for broadcast
        return chatService.sendMessage(message.getSender().getId(), message.getReceiver().getId(), message.getContent());
    }
}
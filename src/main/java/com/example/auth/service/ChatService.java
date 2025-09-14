package com.example.auth.service;

import com.example.auth.entity.User;
import com.example.auth.entity.ChatSession;
import com.example.auth.entity.Message;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.ChatSessionRepository;
import com.example.auth.repository.MessageRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import com.example.auth.dto.response.MessageHistoryResponse;

@Service
public class ChatService {
    private final UserRepository userRepo;
    private final ChatSessionRepository chatSessionRepo;
    private final MessageRepository messageRepo;

    public ChatSessionRepository getChatSessionRepo() {
        return chatSessionRepo;
    }

    public ChatService(UserRepository userRepo, ChatSessionRepository chatSessionRepo, MessageRepository messageRepo) {
        this.userRepo = userRepo;
        this.chatSessionRepo = chatSessionRepo;
        this.messageRepo = messageRepo;
    }

    public String createChatSession(Long userAId, Long userBId, String content) {
        if (userAId.equals(userBId)) return "Cannot chat with yourself";
        User userA = userRepo.findById(userAId).orElseThrow();
        User userB = userRepo.findById(userBId).orElseThrow();
        String roleA = userA.getRole();
        String roleB = userB.getRole();

        boolean valid =
                ("SELLER".equals(roleA) && "CUSTOMER".equals(roleB)) ||
                        ("CUSTOMER".equals(roleA) && "ADMIN".equals(roleB)) ||
                        ("SELLER".equals(roleA) && "ADMIN".equals(roleB)) ||
                        ("CUSTOMER".equals(roleA) && "SELLER".equals(roleB)) ||
                        ("ADMIN".equals(roleA) && "CUSTOMER".equals(roleB)) ||
                        ("ADMIN".equals(roleA) && "SELLER".equals(roleB));

        // Check for existing session both ways
        Optional<ChatSession> session = chatSessionRepo.findByUser1AndUser2(userA, userB);
        if (session.isEmpty())
            session = chatSessionRepo.findByUser1AndUser2(userB, userA);
        if (session.isPresent()) return "Chat session already exists between these users";
        if (!valid) return "Chat not allowed between these roles";

        // Create session
        ChatSession chatSession = new ChatSession();
        chatSession.setUser1(userA);
        chatSession.setUser2(userB);
        chatSessionRepo.save(chatSession);

        // Save initial message
        Message msg = new Message();
        msg.setSender(userA);
        msg.setReceiver(userB);
        msg.setContent(content);
        msg.setMessageType("TEXT");
        msg.setStatus("WAIT");
        msg.setCreatedAt(java.time.LocalDateTime.now());
        msg.setOrder(null);
        messageRepo.save(msg);

        return "Chat session created and message sent";
    }

    public Message sendMessage(Long fromId, Long toId, String content) {
        User from = userRepo.findById(fromId).orElseThrow();
        User to = userRepo.findById(toId).orElseThrow();
        Optional<ChatSession> session = chatSessionRepo.findByUser1AndUser2(from, to);
        if (session.isEmpty())
            session = chatSessionRepo.findByUser1AndUser2(to, from);
        if (session.isEmpty())
            session = chatSessionRepo.findByUser2AndUser1(from, to);
        if (session.isEmpty())
            session = chatSessionRepo.findByUser2AndUser1(to, from);
        if (session.isEmpty()) throw new RuntimeException("No chat session found");

        Message msg = new Message();
        msg.setSender(from);
        msg.setReceiver(to);
        msg.setContent(content);
        msg.setMessageType("TEXT");
        msg.setStatus("WAIT");
        msg.setCreatedAt(java.time.LocalDateTime.now());
        msg.setOrder(null);
        return messageRepo.save(msg);
    }

    public void markMessagesAsSeen(Long userId, Long fromId) {
        List<Message> msgs = messageRepo.findBySenderIdAndReceiverIdAndStatus(fromId, userId, "WAIT");
        for (Message m : msgs) {
            m.setStatus("SEE");
            messageRepo.save(m);
        }
    }

    public List<Map<String, Object>> getConversations(Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        List<ChatSession> sessions = chatSessionRepo.findByUser1OrUser2(user, user);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ChatSession s : sessions) {
            User other = s.getUser1().getId().equals(userId) ? s.getUser2() : s.getUser1();
            boolean hasUnread = messageRepo.existsBySenderIdAndReceiverIdAndStatus(other.getId(), userId, "WAIT");
            Map<String, Object> map = new HashMap<>();
            map.put("userId", other.getId());
            map.put("username", other.getUsername());
            map.put("hasUnread", hasUnread);
            result.add(map);
        }
        return result;
    }

    public List<MessageHistoryResponse> getChatHistory(Long userId, Long otherUserId) {
        List<Message> messages = messageRepo.findByUsers(userId, otherUserId);
        messages.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())); // Newest first
        List<MessageHistoryResponse> result = new ArrayList<>();
        for (Message m : messages) {
            result.add(new MessageHistoryResponse(m.getSender().getId(), m.getContent(), m.getCreatedAt()));
        }
        return result;
    }
}
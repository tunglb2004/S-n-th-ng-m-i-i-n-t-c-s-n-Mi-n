package com.example.auth.repository;

import com.example.auth.entity.ChatSession;
import com.example.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findByUser1AndUser2(User user1, User user2);
    Optional<ChatSession> findByUser2AndUser1(User user1, User user2);
    List<ChatSession> findByUser1OrUser2(User user1, User user2);
}
// src/main/java/com/example/auth/entity/ChatSession.java
package com.example.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_sessions", uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"}))
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne @JoinColumn(name = "user2_id")
    private User user2;

    // Getter & Setter
    public Long getId() { return id; }
    public User getUser1() { return user1; }
    public User getUser2() { return user2; }
    public void setId(Long id) { this.id = id; }
    public void setUser1(User user1) { this.user1 = user1; }
    public void setUser2(User user2) { this.user2 = user2; }
}
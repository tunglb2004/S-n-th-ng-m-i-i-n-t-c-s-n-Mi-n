package com.example.auth.dto.response;

import com.example.auth.entity.Message;

public class MessageResponse {
    private Long id;
    private UserSimpleResponse sender;
    private UserSimpleResponse receiver;
    private Long order; // order id or null
    private String content;
    private String messageType;
    private java.time.LocalDateTime createdAt;
    private String status;

    public MessageResponse(Message msg) {
        this.id = msg.getId();
        this.sender = new UserSimpleResponse(
                msg.getSender().getId(),
                msg.getSender().getUsername(),
                msg.getSender().getEmail()
        );
        this.receiver = new UserSimpleResponse(
                msg.getReceiver().getId(),
                msg.getReceiver().getUsername(),
                msg.getReceiver().getEmail()
        );
        this.order = msg.getOrder() != null ? msg.getOrder().getId() : null;
        this.content = msg.getContent();
        this.messageType = msg.getMessageType();
        this.createdAt = msg.getCreatedAt();
        this.status = msg.getStatus();
    }

    // Getters
    public Long getId() { return id; }
    public UserSimpleResponse getSender() { return sender; }
    public UserSimpleResponse getReceiver() { return receiver; }
    public Long getOrder() { return order; }
    public String getContent() { return content; }
    public String getMessageType() { return messageType; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }
}
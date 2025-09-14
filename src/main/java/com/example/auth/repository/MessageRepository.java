package com.example.auth.repository;

import com.example.auth.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, String status);
    boolean existsBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, String status);

    // Java
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :userId AND m.receiver.id = :otherUserId) OR " +
            "(m.sender.id = :otherUserId AND m.receiver.id = :userId)")
    List<Message> findByUsers(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);
}
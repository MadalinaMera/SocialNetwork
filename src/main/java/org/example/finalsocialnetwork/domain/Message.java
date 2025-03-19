package org.example.finalsocialnetwork.domain;

import java.time.LocalDateTime;

public class Message extends Entity<Long>{
    private Long id;
    private String text;
    private LocalDateTime timestamp;
    private Long senderId;
    private Long receiverId;

    public Message(String text, LocalDateTime timestamp, Long senderId, Long receiverId) {
        this.text = text;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public String toString() {
        return text + '\'' +  timestamp ;
    }
}

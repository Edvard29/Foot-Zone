package com.example.footzone.model;

public class ChatMessage {
    private String userId;
    private String userName;
    private String messageText;
    private long timestamp;

    public ChatMessage() {
        // Пустой конструктор для Firebase
    }

    public ChatMessage(String userId, String userName, String messageText, long timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessageText() {
        return messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

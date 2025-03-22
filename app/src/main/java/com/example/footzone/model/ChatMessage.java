package com.example.footzone.model;

public class ChatMessage {
    private String userName;
    private String messageText;

    public ChatMessage(String userName, String messageText) {
        this.userName = userName;
        this.messageText = messageText;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessageText() {
        return messageText;
    }
}

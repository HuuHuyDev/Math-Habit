package com.kidsapp.data.model;

public class ChatMessage {
    private String id;
    private String senderId;
    private String senderName;
    private String message;
    private long timestamp;
    private boolean isMe;

    public ChatMessage() {}

    public ChatMessage(String id, String senderId, String senderName, String message, long timestamp, boolean isMe) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.timestamp = timestamp;
        this.isMe = isMe;
    }

    // Getters
    public String getId() { return id; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public boolean isMe() { return isMe; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setMe(boolean me) { isMe = me; }
}

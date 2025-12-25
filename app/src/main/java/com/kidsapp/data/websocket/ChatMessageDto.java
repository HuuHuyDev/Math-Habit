package com.kidsapp.data.websocket;

import com.google.gson.annotations.SerializedName;

/**
 * DTO cho tin nhắn chat (mapping với server response)
 */
public class ChatMessageDto {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("roomId")
    private String roomId;
    
    @SerializedName("senderId")
    private String senderId;
    
    @SerializedName("senderName")
    private String senderName;
    
    @SerializedName("senderAvatar")
    private String senderAvatar;
    
    @SerializedName("content")
    private String content;
    
    @SerializedName("messageType")
    private String messageType; // TEXT, IMAGE, STICKER
    
    @SerializedName("mediaUrl")
    private String mediaUrl;
    
    @SerializedName("status")
    private String status; // SENT, DELIVERED, READ
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("readAt")
    private String readAt;

    // Getters
    public String getId() { return id; }
    public String getRoomId() { return roomId; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getSenderAvatar() { return senderAvatar; }
    public String getContent() { return content; }
    public String getMessageType() { return messageType; }
    public String getMediaUrl() { return mediaUrl; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getReadAt() { return readAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }
    public void setContent(String content) { this.content = content; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setReadAt(String readAt) { this.readAt = readAt; }
}

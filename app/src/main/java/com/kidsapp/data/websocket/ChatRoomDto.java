package com.kidsapp.data.websocket;

import com.google.gson.annotations.SerializedName;

/**
 * DTO cho phòng chat (mapping với server response)
 */
public class ChatRoomDto {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("otherUserId")
    private String otherUserId;
    
    @SerializedName("otherUserName")
    private String otherUserName;
    
    @SerializedName("otherUserAvatar")
    private String otherUserAvatar;
    
    @SerializedName("roomType")
    private String roomType; // CHILD_CHILD, PARENT_CHILD
    
    @SerializedName("lastMessage")
    private String lastMessage;
    
    @SerializedName("lastMessageAt")
    private String lastMessageAt;
    
    @SerializedName("unreadCount")
    private int unreadCount;
    
    @SerializedName("isOnline")
    private boolean isOnline;

    // Getters
    public String getId() { return id; }
    public String getOtherUserId() { return otherUserId; }
    public String getOtherUserName() { return otherUserName; }
    public String getOtherUserAvatar() { return otherUserAvatar; }
    public String getRoomType() { return roomType; }
    public String getLastMessage() { return lastMessage; }
    public String getLastMessageAt() { return lastMessageAt; }
    public int getUnreadCount() { return unreadCount; }
    public boolean isOnline() { return isOnline; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setOtherUserId(String otherUserId) { this.otherUserId = otherUserId; }
    public void setOtherUserName(String otherUserName) { this.otherUserName = otherUserName; }
    public void setOtherUserAvatar(String otherUserAvatar) { this.otherUserAvatar = otherUserAvatar; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public void setLastMessageAt(String lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
    public void setOnline(boolean online) { isOnline = online; }
}

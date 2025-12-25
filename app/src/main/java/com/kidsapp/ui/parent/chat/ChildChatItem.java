package com.kidsapp.ui.parent.chat;

/**
 * Model cho item chat với con trong danh sách
 */
public class ChildChatItem {
    
    private String id;
    private String name;
    private String avatarUrl;
    private String lastMessage;
    private String lastMessageTime;
    private int unreadCount;
    private boolean isOnline;
    private int level;

    public ChildChatItem() {}

    public ChildChatItem(String id, String name, String avatarUrl, String lastMessage,
                         String lastMessageTime, int unreadCount, boolean isOnline, int level) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
        this.isOnline = isOnline;
        this.level = level;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
}

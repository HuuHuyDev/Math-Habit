package com.kidsapp.ui.child.chat;

/**
 * Model cho cuộc trò chuyện
 */
public class Conversation {
    
    public static final int TYPE_PARENT = 0;
    public static final int TYPE_FRIEND = 1;

    private String id;
    private String name;
    private String avatar;
    private String lastMessage;
    private String lastMessageTime;
    private int unreadCount;
    private boolean isOnline;
    private int type; // TYPE_PARENT hoặc TYPE_FRIEND

    public Conversation(String id, String name, String avatar, String lastMessage, 
                       String lastMessageTime, int unreadCount, boolean isOnline, int type) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
        this.isOnline = isOnline;
        this.type = type;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAvatar() { return avatar; }
    public String getLastMessage() { return lastMessage; }
    public String getLastMessageTime() { return lastMessageTime; }
    public int getUnreadCount() { return unreadCount; }
    public boolean isOnline() { return isOnline; }
    public int getType() { return type; }
}

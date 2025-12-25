package com.kidsapp.ui.child.chat;

/**
 * Model cho tin nhắn chat
 */
public class ChatMessage {
    
    // Status constants
    public static final int STATUS_SENDING = 0;
    public static final int STATUS_SENT = 1;
    public static final int STATUS_DELIVERED = 2;
    public static final int STATUS_READ = 3;
    public static final int STATUS_FAILED = -1;
    
    private String id;
    private String content;
    private String time;
    private boolean isFromCurrentUser; // true = từ user hiện tại
    private int status = STATUS_SENT;
    private String senderName;
    private String senderAvatar;

    public ChatMessage(String id, String content, String time, boolean isFromCurrentUser) {
        this.id = id;
        this.content = content;
        this.time = time;
        this.isFromCurrentUser = isFromCurrentUser;
    }

    // Getters
    public String getId() { return id; }
    public String getContent() { return content; }
    public String getTime() { return time; }
    public boolean isFromCurrentUser() { return isFromCurrentUser; }
    public boolean isFromChild() { return isFromCurrentUser; } // Backward compatibility
    public int getStatus() { return status; }
    public String getSenderName() { return senderName; }
    public String getSenderAvatar() { return senderAvatar; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setTime(String time) { this.time = time; }
    public void setFromCurrentUser(boolean fromCurrentUser) { isFromCurrentUser = fromCurrentUser; }
    public void setStatus(int status) { this.status = status; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }
}

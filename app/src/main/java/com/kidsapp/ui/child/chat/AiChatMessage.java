package com.kidsapp.ui.child.chat;

/**
 * Model cho tin nhắn trong AI Chat
 */
public class AiChatMessage {
    
    private String id;
    private String content;
    private String time;
    private boolean isFromUser; // true = user, false = AI

    public AiChatMessage() {}

    public AiChatMessage(String id, String content, String time, boolean isFromUser) {
        this.id = id;
        this.content = content;
        this.time = time;
        this.isFromUser = isFromUser;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isFromUser() { return isFromUser; }
    public void setFromUser(boolean fromUser) { isFromUser = fromUser; }
}

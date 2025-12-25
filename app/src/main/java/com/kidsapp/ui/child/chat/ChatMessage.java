package com.kidsapp.ui.child.chat;

/**
 * Model cho tin nhắn chat
 */
public class ChatMessage {
    
    private String id;
    private String content;
    private String time;
    private boolean isFromChild; // true = từ trẻ, false = từ phụ huynh

    public ChatMessage(String id, String content, String time, boolean isFromChild) {
        this.id = id;
        this.content = content;
        this.time = time;
        this.isFromChild = isFromChild;
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public String getTime() { return time; }
    public boolean isFromChild() { return isFromChild; }
}

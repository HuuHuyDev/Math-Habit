package com.kidsapp.data.websocket;

/**
 * Request class for sending chat messages
 */
public class ChatMessageRequest {
    private String roomId;
    private String content;
    private String messageType; // TEXT, IMAGE, AUDIO, VIDEO, FILE

    public ChatMessageRequest() {}

    public ChatMessageRequest(String roomId, String content, String messageType) {
        this.roomId = roomId;
        this.content = content;
        this.messageType = messageType;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}

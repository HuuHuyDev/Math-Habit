package com.kidsapp.data.request;

/**
 * Request model cho AI Chat API
 */
public class AiChatRequest {
    
    private String message;
    private String conversationId;
    private String mode; // CHILD, PARENT, CUSTOM
    private String customPrompt;

    public AiChatRequest() {}

    public AiChatRequest(String message, String mode) {
        this.message = message;
        this.mode = mode;
    }

    public AiChatRequest(String message, String conversationId, String mode) {
        this.message = message;
        this.conversationId = conversationId;
        this.mode = mode;
    }

    // Getters & Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getCustomPrompt() { return customPrompt; }
    public void setCustomPrompt(String customPrompt) { this.customPrompt = customPrompt; }
}

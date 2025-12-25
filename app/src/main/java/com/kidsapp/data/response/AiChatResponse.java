package com.kidsapp.data.response;

/**
 * Response model cho AI Chat API
 */
public class AiChatResponse {
    
    private boolean success;
    private String message;
    private ChatData data;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public ChatData getData() { return data; }
    public void setData(ChatData data) { this.data = data; }

    /**
     * Inner class cho data
     */
    public static class ChatData {
        private String message;
        private String conversationId;
        private long timestamp;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}

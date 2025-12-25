package com.kidsapp.data.websocket;

import android.util.Log;

import com.kidsapp.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

/**
 * Singleton quản lý kết nối WebSocket
 * Sử dụng STOMP protocol để giao tiếp với Spring Boot server
 */
public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    
    private static WebSocketManager instance;
    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;
    private String currentUserId;
    private boolean isConnected = false;
    
    // Listeners
    private List<WebSocketListener> listeners = new ArrayList<>();
    private Map<String, Disposable> subscriptions = new HashMap<>();

    private WebSocketManager() {
        compositeDisposable = new CompositeDisposable();
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    /**
     * Kết nối WebSocket với userId
     */
    public void connect(String userId) {
        if (isConnected && userId.equals(currentUserId)) {
            Log.d(TAG, "Already connected with userId: " + userId);
            return;
        }

        this.currentUserId = userId;
        
        // Sử dụng WebSocket URL từ Constants
        String wsUrl = Constants.WS_URL;
        
        Log.d(TAG, "Connecting to: " + wsUrl);

        // Tạo STOMP client
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);
        
        // Headers khi connect
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("userId", userId));

        // Lắng nghe lifecycle
        Disposable lifecycleDisposable = stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "WebSocket OPENED");
                            isConnected = true;
                            notifyConnected();
                            subscribeToUserTopics();
                            break;
                        case CLOSED:
                            Log.d(TAG, "WebSocket CLOSED");
                            isConnected = false;
                            notifyDisconnected();
                            break;
                        case ERROR:
                            Log.e(TAG, "WebSocket ERROR", lifecycleEvent.getException());
                            isConnected = false;
                            notifyError(lifecycleEvent.getException());
                            break;
                    }
                }, throwable -> {
                    Log.e(TAG, "Lifecycle error", throwable);
                });

        compositeDisposable.add(lifecycleDisposable);
        
        // Kết nối
        stompClient.connect(headers);
    }

    /**
     * Subscribe vào các topic của user
     */
    private void subscribeToUserTopics() {
        if (currentUserId == null) return;

        // Subscribe nhận tin nhắn mới
        subscribeToTopic("/user/" + currentUserId + "/queue/messages", message -> {
            Log.d(TAG, "Received message: " + message);
            notifyMessageReceived(message);
        });

        // Subscribe xác nhận đã gửi
        subscribeToTopic("/user/" + currentUserId + "/queue/sent", message -> {
            Log.d(TAG, "Message sent confirmation: " + message);
            notifyMessageSent(message);
        });

        // Subscribe thông báo typing
        subscribeToTopic("/user/" + currentUserId + "/queue/typing", message -> {
            Log.d(TAG, "Typing notification: " + message);
            notifyTyping(message);
        });

        // Subscribe read receipt
        subscribeToTopic("/user/" + currentUserId + "/queue/read-receipt", message -> {
            Log.d(TAG, "Read receipt: " + message);
            notifyReadReceipt(message);
        });
    }

    private void subscribeToTopic(String topic, MessageCallback callback) {
        if (stompClient == null || !isConnected) return;

        Disposable disposable = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stompMessage -> {
                    callback.onMessage(stompMessage.getPayload());
                }, throwable -> {
                    Log.e(TAG, "Subscribe error for " + topic, throwable);
                });

        subscriptions.put(topic, disposable);
        compositeDisposable.add(disposable);
    }

    /**
     * Gửi tin nhắn
     */
    public void sendMessage(String receiverId, String content) {
        if (stompClient == null || !isConnected) {
            Log.e(TAG, "Not connected, cannot send message");
            return;
        }

        String json = String.format(
                "{\"receiverId\":\"%s\",\"content\":\"%s\",\"messageType\":\"TEXT\"}",
                receiverId, escapeJson(content)
        );

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("senderId", currentUserId));

        Disposable disposable = stompClient.send("/app/chat.send", json)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d(TAG, "Message sent successfully");
                }, throwable -> {
                    Log.e(TAG, "Send message error", throwable);
                    notifyError(throwable);
                });

        compositeDisposable.add(disposable);
    }

    /**
     * Gửi thông báo đang gõ
     */
    public void sendTyping(String receiverId) {
        if (stompClient == null || !isConnected) return;

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("senderId", currentUserId));

        Disposable disposable = stompClient.send("/app/chat.typing", "\"" + receiverId + "\"")
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {}, throwable -> {
                    Log.e(TAG, "Send typing error", throwable);
                });

        compositeDisposable.add(disposable);
    }

    /**
     * Đánh dấu đã đọc
     */
    public void markAsRead(String roomId) {
        if (stompClient == null || !isConnected) return;

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("userId", currentUserId));

        Disposable disposable = stompClient.send("/app/chat.read", "\"" + roomId + "\"")
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    Log.d(TAG, "Marked as read: " + roomId);
                }, throwable -> {
                    Log.e(TAG, "Mark as read error", throwable);
                });

        compositeDisposable.add(disposable);
    }

    /**
     * Ngắt kết nối
     */
    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        compositeDisposable.clear();
        subscriptions.clear();
        isConnected = false;
        currentUserId = null;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    // ==================== LISTENERS ====================

    public void addListener(WebSocketListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(WebSocketListener listener) {
        listeners.remove(listener);
    }

    private void notifyConnected() {
        for (WebSocketListener listener : listeners) {
            listener.onConnected();
        }
    }

    private void notifyDisconnected() {
        for (WebSocketListener listener : listeners) {
            listener.onDisconnected();
        }
    }

    private void notifyError(Throwable throwable) {
        for (WebSocketListener listener : listeners) {
            listener.onError(throwable);
        }
    }

    private void notifyMessageReceived(String message) {
        for (WebSocketListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }

    private void notifyMessageSent(String message) {
        for (WebSocketListener listener : listeners) {
            listener.onMessageSent(message);
        }
    }

    private void notifyTyping(String senderId) {
        for (WebSocketListener listener : listeners) {
            listener.onTyping(senderId);
        }
    }

    private void notifyReadReceipt(String roomId) {
        for (WebSocketListener listener : listeners) {
            listener.onReadReceipt(roomId);
        }
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    // ==================== INTERFACES ====================

    public interface WebSocketListener {
        void onConnected();
        void onDisconnected();
        void onError(Throwable throwable);
        void onMessageReceived(String message);
        void onMessageSent(String message);
        void onTyping(String senderId);
        void onReadReceipt(String roomId);
    }

    private interface MessageCallback {
        void onMessage(String message);
    }
}

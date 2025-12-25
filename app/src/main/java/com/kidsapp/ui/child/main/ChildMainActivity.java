package com.kidsapp.ui.child.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.kidsapp.R;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.websocket.WebSocketManager;
import com.kidsapp.databinding.ActivityChildMainBinding;
import com.kidsapp.ui.child.chat.ChatHubFragment;
import com.kidsapp.ui.child.chat.ChatRoomFragment;
import com.kidsapp.ui.child.home.ChildHomeFragment;

/**
 * Child Main Activity with FAB Chat and WebSocket
 */
public class ChildMainActivity extends AppCompatActivity {
    
    private static final String TAG = "ChildMainActivity";
    private ActivityChildMainBinding binding;
    private WebSocketManager webSocketManager;
    private SharedPref sharedPref;
    private int unreadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChildMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPref = new SharedPref(this);
        
        setupFabChat();
        setupFragmentListener();
        connectWebSocket();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.childHomeHost, new ChildHomeFragment())
                    .commit();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    /**
     * Kết nối WebSocket khi app khởi động
     */
    private void connectWebSocket() {
        String userId = getCurrentUserId();
        Log.d(TAG, "Connecting WebSocket with userId: " + userId);
        
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.connect(userId);
    }

    private String getCurrentUserId() {
        String userId = sharedPref.getUserId();
        if (userId == null || userId.isEmpty()) {
            userId = sharedPref.getChildId();
        }
        if (userId == null || userId.isEmpty()) {
            // Fallback for testing - thay bằng UUID thực từ database
            userId = "test-child-user-id";
        }
        return userId;
    }

    private void setupFabChat() {
        binding.fabChat.setOnClickListener(v -> openChat());
        
        // Demo: Hiển thị badge với 3 tin nhắn mới
        updateBadge(3);
    }

    private void setupFragmentListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.childHomeHost);
            
            // Ẩn FAB khi đang ở màn hình chat
            if (current instanceof ChatHubFragment || current instanceof ChatRoomFragment) {
                hideFab();
            } else {
                showFab();
            }
        });
    }

    private void openChat() {
        // Reset badge khi mở chat
        updateBadge(0);
        
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.childHomeHost, new ChatHubFragment())
                .addToBackStack("chat")
                .commit();
    }

    /**
     * Cập nhật badge tin nhắn mới
     */
    public void updateBadge(int count) {
        this.unreadCount = count;
        if (count > 0) {
            binding.badgeChat.setVisibility(View.VISIBLE);
            binding.txtBadgeCount.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            binding.badgeChat.setVisibility(View.GONE);
        }
    }

    public void showFab() {
        binding.fabChat.show();
        if (unreadCount > 0) {
            binding.badgeChat.setVisibility(View.VISIBLE);
        }
    }

    public void hideFab() {
        binding.fabChat.hide();
        binding.badgeChat.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ngắt kết nối WebSocket khi app đóng
        if (webSocketManager != null) {
            webSocketManager.disconnect();
        }
    }
}

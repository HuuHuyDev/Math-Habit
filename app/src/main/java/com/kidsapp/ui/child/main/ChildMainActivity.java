package com.kidsapp.ui.child.main;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.kidsapp.R;
import com.kidsapp.databinding.ActivityChildMainBinding;
import com.kidsapp.ui.child.chat.ChatHubFragment;
import com.kidsapp.ui.child.chat.ChatRoomFragment;
import com.kidsapp.ui.child.home.ChildHomeFragment;

/**
 * Child Main Activity with FAB Chat
 */
public class ChildMainActivity extends AppCompatActivity {
    
    private ActivityChildMainBinding binding;
    private int unreadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChildMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupFabChat();
        setupFragmentListener();

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
}

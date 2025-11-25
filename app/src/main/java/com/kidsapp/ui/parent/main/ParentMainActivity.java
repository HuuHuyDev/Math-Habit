package com.kidsapp.ui.parent.main;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.kidsapp.R;
import com.kidsapp.databinding.ActivityParentMainBinding;

/**
 * Parent Main Activity with Custom Bottom Navigation
 * - Chỉ bọc icon khi active
 * - Icon active: trắng, inactive: #9CA3AF
 * - Text active: #2563EB, inactive: #9CA3AF
 */
public class ParentMainActivity extends AppCompatActivity {
    private ActivityParentMainBinding binding;
    private NavController navController;
    
    // Navigation items
    private LinearLayout[] navItems;
    private FrameLayout[] iconContainers;
    private ImageView[] icons;
    private TextView[] labels;
    private int currentSelectedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParentMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Đợi view được tạo hoàn toàn trước khi setup navigation
        if (binding != null && binding.getRoot() != null) {
            binding.getRoot().post(() -> {
                setupNavigation();
                setupNav();
            });
        }
    }

    private void setupNavigation() {
        try {
            // Kiểm tra xem view có tồn tại không
            if (binding == null || binding.getRoot() == null) {
                return;
            }
            
            // Tìm NavHostFragment
            androidx.fragment.app.Fragment navHostFragment = getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);
            
            if (navHostFragment == null) {
                // Nếu chưa có, đợi thêm một chút
                binding.getRoot().postDelayed(() -> setupNavigation(), 100);
                return;
            }
            
            // Get NavController
            navController = Navigation.findNavController(this, R.id.navHostFragment);
            
            // KHÔNG setup ActionBar vì app đang dùng custom navigation
            // và không có ActionBar/Toolbar
            
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, thử lại sau khi view đã được attach
            if (binding != null && binding.getRoot() != null) {
                binding.getRoot().postDelayed(() -> {
                    try {
                        navController = Navigation.findNavController(this, R.id.navHostFragment);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }, 300);
            }
        }
    }

    private void setupNav() {
        try {
            // Initialize arrays
            navItems = new LinearLayout[]{
                findViewById(R.id.nav_home),
                findViewById(R.id.nav_tasks),
                findViewById(R.id.nav_report),
                findViewById(R.id.nav_children),
                findViewById(R.id.nav_profile)
            };

            iconContainers = new FrameLayout[]{
                findViewById(R.id.iconContainer_home),
                findViewById(R.id.iconContainer_tasks),
                findViewById(R.id.iconContainer_report),
                findViewById(R.id.iconContainer_children),
                findViewById(R.id.iconContainer_profile)
            };

            icons = new ImageView[]{
                findViewById(R.id.icon_home),
                findViewById(R.id.icon_tasks),
                findViewById(R.id.icon_report),
                findViewById(R.id.icon_children),
                findViewById(R.id.icon_profile)
            };

            labels = new TextView[]{
                findViewById(R.id.label_home),
                findViewById(R.id.label_tasks),
                findViewById(R.id.label_report),
                findViewById(R.id.label_children),
                findViewById(R.id.label_profile)
            };

            // Initialize all icons with inactive color
            for (int i = 0; i < icons.length; i++) {
                icons[i].setColorFilter(ContextCompat.getColor(this, R.color.nav_icon_inactive));
            }

            // Setup click listeners
            for (int i = 0; i < navItems.length; i++) {
                int index = i;
                navItems[i].setOnClickListener(v -> {
                    if (navController != null) {
                        setActive(index);
                    }
                });
            }

            // Select home tab by default
            setActive(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setActive(int index) {
        if (index < 0 || index >= navItems.length || navController == null) {
            return;
        }

        // Reset all items
        for (int i = 0; i < navItems.length; i++) {
            // Reset icon container background
            iconContainers[i].setBackgroundResource(R.drawable.bg_icon_inactive);
            
            // Reset icon color (inactive: #9CA3AF)
            icons[i].setColorFilter(ContextCompat.getColor(this, R.color.nav_icon_inactive));
            
            // Reset text color (inactive: #9CA3AF)
            labels[i].setTextColor(ContextCompat.getColor(this, R.color.nav_text_inactive));
            
            // Reset scale animation
            iconContainers[i].animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
        }

        // Activate selected item
        // Set icon container background to gradient
        iconContainers[index].setBackgroundResource(R.drawable.bg_icon_active);
        
        // Set icon color to white (active)
        icons[index].setColorFilter(ContextCompat.getColor(this, R.color.white));
        
        // Set text color to blue (active: #2563EB)
        labels[index].setTextColor(ContextCompat.getColor(this, R.color.nav_text_active));
        
        // Scale animation for icon container (1.0 → 1.1)
        iconContainers[index].animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).start();

        currentSelectedIndex = index;

        // Navigate to fragment
        navigateToFragment(index);
    }

    private void navigateToFragment(int index) {
        if (navController == null) {
            return;
        }

        try {
            int destinationId;
            switch (index) {
                case 0: // Home
                    destinationId = R.id.nav_home;
                    break;
                case 1: // Tasks (Mục tiêu)
                    destinationId = R.id.nav_tasks;
                    break;
                case 2: // Report
                    destinationId = R.id.nav_report;
                    break;
                case 3: // Children
                    destinationId = R.id.nav_children;
                    break;
                case 4: // Profile
                    destinationId = R.id.nav_profile;
                    break;
                default:
                    destinationId = R.id.nav_home;
                    break;
            }

            // Only navigate if not already at this destination
            if (navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() != destinationId) {
                navController.navigate(destinationId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        try {
            if (navController != null && navController.getCurrentDestination() != null) {
                int currentDestId = navController.getCurrentDestination().getId();
                
                // Nếu đang ở detail fragment, pop back về home
                if (currentDestId == R.id.parentChildDetailFragment) {
                    if (navController.popBackStack()) {
                        setActive(0); // Set home tab active
                        return;
                    }
                }
                // Nếu đang ở detail fragment, pop back về home
                if (currentDestId == R.id.nav_report) {
                    if (navController.popBackStack()) {
                        setActive(5); // Set home tab active
                        return;
                    }
                }
                // Nếu đang ở home, thoát app
                if (currentDestId == R.id.nav_home) {
                    super.onBackPressed();
                    return;
                }
                
                // Các trường hợp khác, thử pop về home
                if (!navController.popBackStack(R.id.nav_home, false)) {
                    // Nếu không pop được, navigate về home
                    navController.navigate(R.id.nav_home);
                }
                setActive(0);
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, gọi super để xử lý mặc định
            super.onBackPressed();
        }
    }
}

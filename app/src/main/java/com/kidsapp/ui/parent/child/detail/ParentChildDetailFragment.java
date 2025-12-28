package com.kidsapp.ui.parent.child.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.model.WeeklyProgress;
import com.kidsapp.databinding.FragmentParentChildDetailBinding;
import com.kidsapp.ui.parent.child.detail.components.DayProgress;
import com.kidsapp.ui.parent.child.detail.components.ProgressAdapter;
import com.kidsapp.ui.components.LoadingDialog;
import com.kidsapp.viewmodel.ChildViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Fragment hiển thị chi tiết của Bé
 */
public class ParentChildDetailFragment extends Fragment {

    private FragmentParentChildDetailBinding binding;
    private ChildViewModel viewModel;
    private LoadingDialog loadingDialog;
    private String childId;
    private String childName;
    private int childLevel;
    private String actualPassword = ""; // Lưu password thật
    private boolean isPasswordVisible = false; // Trạng thái hiển thị password

    public ParentChildDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy arguments được truyền từ ParentHomeFragment
        if (getArguments() != null) {
            childId = getArguments().getString("childId");
            childName = getArguments().getString("childName");
            childLevel = getArguments().getInt("childLevel", 1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentParentChildDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ChildViewModel.class);
        loadingDialog = new LoadingDialog(requireContext());
        
        setupAppBar();
        setupButtons();
        setupPasswordToggle();
        setupProgressChart();
        setupTabLayout();
        
        // Observe ViewModel
        observeViewModel();
        
        // Load child detail từ API
        if (childId != null && !childId.isEmpty()) {
            showLoading();
            viewModel.loadChildDetail(childId);
            viewModel.loadWeeklyProgress(childId);
        } else {
            // Fallback: hiển thị dữ liệu từ arguments
            setupHeaderFromArguments();
        }
    }
    
    private void showLoading() {
        if (loadingDialog != null) {
            loadingDialog.show("Đang tải...");
        }
    }
    
    private void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
    
    /**
     * Observe ViewModel để cập nhật UI khi có dữ liệu
     */
    private void observeViewModel() {
        viewModel.getChildDetail().observe(getViewLifecycleOwner(), child -> {
            if (child != null) {
                updateHeaderFromApi(child);
            }
        });
        
        viewModel.getWeeklyProgress().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null && progress.getDailyProgress() != null) {
                updateProgressChart(progress);
            }
        });
        
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                viewModel.clearMessages();
                // Fallback: hiển thị dữ liệu từ arguments
                setupHeaderFromArguments();
            }
        });
        
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && !isLoading) {
                hideLoading();
            }
        });
    }
    
    /**
     * Cập nhật header từ API response
     */
    private void updateHeaderFromApi(ApiService.ChildResponse child) {
        // Tên và thông tin cơ bản
        String name = child.name != null ? child.name : (child.nickname != null ? child.nickname : "");
        int level = child.level != null ? child.level : (child.currentLevel != null ? child.currentLevel : 1);
        int grade = child.grade != null ? child.grade : 1;
        String className = "Lớp " + grade;
        
        binding.header.txtChildName.setText(name);
        binding.header.txtChildInfo.setText(String.format("%s • Lv %d", className, level));
        binding.header.txtLevel.setText(String.format("Lv %d", level));
        
        // Coins và XP
        int coins = child.coins != null ? child.coins : 0;
        int xp = child.totalXp != null ? child.totalXp : 0;
        binding.header.txtCoin.setText(formatNumber(coins));
        binding.header.txtXP.setText(String.format("%s XP", formatNumber(xp)));
        
        // Avatar - hiển thị emoji nếu có
        if (child.avatarUrl != null && !child.avatarUrl.isEmpty()) {
            if (child.avatarUrl.startsWith("http")) {
                // URL hình ảnh
                Glide.with(this)
                        .load(child.avatarUrl)
                        .placeholder(R.drawable.ic_child_face)
                        .error(R.drawable.ic_child_face)
                        .circleCrop()
                        .into(binding.header.imgChildAvatar);
                binding.header.txtChildEmoji.setVisibility(View.GONE);
                binding.header.imgChildAvatar.setVisibility(View.VISIBLE);
            } else {
                // Emoji
                binding.header.txtChildEmoji.setText(child.avatarUrl);
                binding.header.txtChildEmoji.setVisibility(View.VISIBLE);
                binding.header.imgChildAvatar.setVisibility(View.GONE);
            }
        } else {
            // Mặc định dựa trên giới tính
            String emoji = (child.gender != null && child.gender) ? "👦" : "👧";
            binding.header.txtChildEmoji.setText(emoji);
            binding.header.txtChildEmoji.setVisibility(View.VISIBLE);
            binding.header.imgChildAvatar.setVisibility(View.GONE);
        }
        
        // Thông tin đăng nhập
        // Username: ưu tiên từ API, fallback từ arguments
        String username = child.username;
        if (username == null || username.isEmpty()) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                username = arguments.getString("username", "");
            }
        }
        if (username != null && !username.isEmpty()) {
            binding.txtUsername.setText(username);
        } else {
            binding.txtUsername.setText("Chưa có");
        }
        
        // Password: không được trả về từ API (bảo mật)
        // Hiển thị thông báo cho parent biết
        binding.txtPassword.setText("••••••••");
        actualPassword = ""; // Không có password thật để hiển thị
        
        // Cập nhật biến local
        this.childName = name;
        this.childLevel = level;
    }
    
    /**
     * Hiển thị header từ arguments (fallback)
     */
    private void setupHeaderFromArguments() {
        Bundle arguments = getArguments();
        String name = "Bé";
        String className = "Lớp 1";
        int level = 1;
        int coin = 0;
        int xp = 0;
        String username = "";
        String password = "";

        if (arguments != null) {
            name = arguments.getString("childName", name);
            level = arguments.getInt("childLevel", level);
            xp = arguments.getInt("childXP", xp);
            username = arguments.getString("username", "");
            password = arguments.getString("password", "");
            className = "Lớp " + level;
        }

        binding.header.txtChildName.setText(name);
        binding.header.txtChildInfo.setText(String.format("%s • Lv %d", className, level));
        binding.header.txtLevel.setText(String.format("Lv %d", level));
        binding.header.txtCoin.setText(formatNumber(coin));
        binding.header.txtXP.setText(String.format("%s XP", formatNumber(xp)));

        if (!username.isEmpty()) {
            binding.txtUsername.setText(username);
        }
        if (!password.isEmpty()) {
            actualPassword = password;
            binding.txtPassword.setText("••••••••");
        }
        
        // Default avatar
        binding.header.imgChildAvatar.setImageResource(R.drawable.ic_child_face);
    }

    /**
     * Thiết lập AppBar: gắn sự kiện click và quản lý badge thông báo
     */
    private void setupAppBar() {
        // Sự kiện click nút Back - gọi onBackPressed của Activity để xử lý logic
        binding.appbar.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    /**
     * Thiết lập sự kiện cho 2 buttons
     */
    private void setupButtons() {
        // Button Chỉnh mục tiêu tuần
        binding.btnEditWeeklyGoal.setOnClickListener(v -> {
            if (childId == null || childId.isEmpty()) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin bé", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                // Tạo Bundle để truyền childId và childName
                Bundle args = new Bundle();
                args.putString("childId", childId);
                args.putString("childName", childName);
                args.putInt("childLevel", childLevel);
                
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_childDetail_to_weeklyGoal, args);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Không thể mở chỉnh mục tiêu", Toast.LENGTH_SHORT).show();
            }
        });

        // Button Xem báo cáo chi tiết
        binding.btnViewDetailReport.setOnClickListener(v -> {
            if (childId == null || childId.isEmpty()) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin bé", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Tạo Bundle để truyền childId và childName
                Bundle args = new Bundle();
                args.putString("childId", childId);
                args.putString("childName", childName);

                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_parentChildDetailFragment_to_nav_report, args);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Không thể mở báo cáo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Thiết lập toggle hiển thị/ẩn password
     */
    private void setupPasswordToggle() {
        binding.btnTogglePassword.setOnClickListener(v -> {
            // Password không được trả về từ API vì lý do bảo mật
            // Hiển thị thông báo cho parent
            if (actualPassword == null || actualPassword.isEmpty()) {
                Toast.makeText(requireContext(), 
                    "Mật khẩu không được hiển thị vì lý do bảo mật. Bạn có thể đặt lại mật khẩu mới khi chỉnh sửa thông tin bé.", 
                    Toast.LENGTH_LONG).show();
                return;
            }
            
            isPasswordVisible = !isPasswordVisible;
            
            if (isPasswordVisible) {
                // Hiển thị password thật
                binding.txtPassword.setText(actualPassword);
                binding.btnTogglePassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                // Ẩn password
                binding.txtPassword.setText("••••••••");
                binding.btnTogglePassword.setImageResource(R.drawable.ic_eye);
            }
        });
    }

    /**
     * Thiết lập biểu đồ tiến độ tuần hiện tại (mặc định)
     */
    private void setupProgressChart() {
        // Tạo danh sách dữ liệu mặc định 7 ngày (T2-CN)
        List<DayProgress> progressList = new ArrayList<>();
        progressList.add(new DayProgress("T2", 0));
        progressList.add(new DayProgress("T3", 0));
        progressList.add(new DayProgress("T4", 0));
        progressList.add(new DayProgress("T5", 0));
        progressList.add(new DayProgress("T6", 0));
        progressList.add(new DayProgress("T7", 0));
        progressList.add(new DayProgress("CN", 0));

        // Tạo adapter và set cho RecyclerView
        ProgressAdapter adapter = new ProgressAdapter(progressList);
        binding.progressChart.recyclerProgress.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.progressChart.recyclerProgress.setAdapter(adapter);
    }

    /**
     * Cập nhật biểu đồ tiến độ từ API response
     */
    private void updateProgressChart(WeeklyProgress progress) {
        if (progress.getDailyProgress() == null || progress.getDailyProgress().isEmpty()) {
            return;
        }

        List<DayProgress> progressList = new ArrayList<>();
        for (WeeklyProgress.DailyProgress daily : progress.getDailyProgress()) {
            String dayLabel = daily.getDayLabel() != null ? daily.getDayLabel() : "";
            int percent = daily.getProgressPercent();
            progressList.add(new DayProgress(dayLabel, percent));
        }

        // Cập nhật adapter
        ProgressAdapter adapter = new ProgressAdapter(progressList);
        binding.progressChart.recyclerProgress.setAdapter(adapter);
    }

    /**
     * Thiết lập TabLayout và ViewPager2 với custom tab
     */
    private void setupTabLayout() {
        // Setup ViewPager2 với adapter - truyền childId
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(requireActivity(), childId);
        binding.viewPager.setAdapter(pagerAdapter);

        // Kết nối TabLayout với ViewPager2 và set custom view cho từng tab
        new TabLayoutMediator(
                binding.tabLayout,
                binding.viewPager,
                (tab, position) -> {
                    // Tạo custom view cho tab - inflate với parent để match_parent hoạt động
                    View customView = LayoutInflater.from(requireContext())
                            .inflate(R.layout.tab_item, binding.tabLayout, false);

                    ImageView imgIcon = customView.findViewById(R.id.imgTabIcon);
                    TextView txtText = customView.findViewById(R.id.txtTabText);

                    switch (position) {
                        case 0: // Việc nhà
                            imgIcon.setImageResource(R.drawable.ic_housework_gray);
                            txtText.setText(getString(R.string.tab_housework));
                            txtText.setTextColor(0xFF4A4A4A);
                            break;
                        case 1: // Bài tập
                            imgIcon.setImageResource(R.drawable.ic_exercise_gray);
                            txtText.setText(getString(R.string.tab_exercise));
                            txtText.setTextColor(0xFF4A4A4A);
                            break;
                        case 2: // Huy hiệu
                            imgIcon.setImageResource(R.drawable.ic_badge_gray);
                            txtText.setText(getString(R.string.tab_badge));
                            txtText.setTextColor(0xFF4A4A4A);
                            break;
                    }

                    tab.setCustomView(customView);
                }
        ).attach();

        // Xử lý sự kiện khi tab được chọn để đổi màu và background
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabView(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabView(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Không cần xử lý
            }
        });

        // Set tab đầu tiên được chọn
        if (binding.tabLayout.getTabCount() > 0) {
            TabLayout.Tab firstTab = binding.tabLayout.getTabAt(0);
            if (firstTab != null) {
                updateTabView(firstTab, true);
            }
        }
    }

    /**
     * Cập nhật giao diện tab (màu, background, icon) dựa trên trạng thái selected
     */
    private void updateTabView(TabLayout.Tab tab, boolean isSelected) {
        View customView = tab.getCustomView();
        if (customView == null) return;

        TextView txtText = customView.findViewById(R.id.txtTabText);
        ImageView imgIcon = customView.findViewById(R.id.imgTabIcon);
        View rootView = customView.findViewById(R.id.tab_item_root);

        if (rootView == null) {
            rootView = customView;
        }

        if (isSelected) {
            // Tab được chọn: gradient xanh, icon và text màu trắng
            rootView.setBackgroundResource(R.drawable.tab_selected_bg);
            if (txtText != null) {
                txtText.setTextColor(0xFFFFFFFF);
            }

            // Đổi icon sang màu trắng
            int position = tab.getPosition();
            if (imgIcon != null) {
                switch (position) {
                    case 0:
                        imgIcon.setImageResource(R.drawable.ic_housework);
                        break;
                    case 1:
                        imgIcon.setImageResource(R.drawable.ic_exercise);
                        break;
                    case 2:
                        imgIcon.setImageResource(R.drawable.ic_badge);
                        break;
                }
            }
        } else {
            // Tab chưa được chọn: nền trắng, icon và text màu xám đậm
            rootView.setBackgroundResource(R.drawable.tab_unselected_bg);
            if (txtText != null) {
                txtText.setTextColor(0xFF4A4A4A);
            }

            // Đổi icon sang màu xám
            int position = tab.getPosition();
            if (imgIcon != null) {
                switch (position) {
                    case 0:
                        imgIcon.setImageResource(R.drawable.ic_housework_gray);
                        break;
                    case 1:
                        imgIcon.setImageResource(R.drawable.ic_exercise_gray);
                        break;
                    case 2:
                        imgIcon.setImageResource(R.drawable.ic_badge_gray);
                        break;
                }
            }
        }
    }

    /**
     * Format số với dấu phẩy (ví dụ: 1234 -> "1,234")
     */
    private String formatNumber(int number) {
        return String.format("%,d", number);
    }

    /**
     * Method để cập nhật dữ liệu header từ bên ngoài
     * Có thể gọi từ ViewModel observer hoặc khi nhận dữ liệu mới
     */
    public void updateHeaderData(String name, String className, int level, int coin, int xp, String avatarUrl) {
        if (binding != null && binding.header != null) {
            binding.header.txtChildName.setText(name);
            binding.header.txtChildInfo.setText(String.format("%s • Lv %d", className, level));
            binding.header.txtLevel.setText(String.format("Lv %d", level));
            binding.header.txtCoin.setText(formatNumber(coin));
            binding.header.txtXP.setText(String.format("%s XP", formatNumber(xp)));

            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(this)
                        .load(avatarUrl)
                        .placeholder(R.drawable.ic_child_face)
                        .error(R.drawable.ic_child_face)
                        .circleCrop()
                        .into(binding.header.imgChildAvatar);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideLoading();
        loadingDialog = null;
        binding = null;
    }
}
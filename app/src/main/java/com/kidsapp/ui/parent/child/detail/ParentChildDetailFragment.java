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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.kidsapp.R;
import com.kidsapp.databinding.FragmentParentChildDetailBinding;
import com.kidsapp.ui.parent.child.detail.components.DayProgress;
import com.kidsapp.ui.parent.child.detail.components.ProgressAdapter;
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
    private String childId;
    private String childName;
    private int childLevel;

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

        setupAppBar();
        setupButtons(); // Thêm setup buttons
        setupHeader();
        setupProgressChart();
        setupTabLayout();
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
     * Thiết lập và bind dữ liệu cho Header Bé
     * Lấy dữ liệu từ Bundle arguments hoặc dùng dữ liệu mẫu
     */
    private void setupHeader() {
        // Lấy dữ liệu từ Bundle arguments (nếu có)
        Bundle arguments = getArguments();
        String childName = "Hồ Hữu Huy"; // Default
        String childClass = "Lớp 1"; // Default
        int childLevel = 4; // Default
        int coin = 1234; // Default
        int xp = 1234; // Default
        String avatarUrl = null; // URL avatar từ server (nếu có)

        if (arguments != null) {
            // Lấy dữ liệu từ Bundle
            childName = arguments.getString("childName", childName);
            childLevel = arguments.getInt("childLevel", childLevel);
            xp = arguments.getInt("childXP", xp);
            // Tính lớp từ level (hoặc có thể truyền riêng)
            childClass = "Lớp " + childLevel;
            // TODO: Lấy coin từ arguments nếu có
            // coin = arguments.getInt("childCoin", coin);
        }

        // Bind dữ liệu vào các view trong header
        binding.header.txtChildName.setText(childName);
        binding.header.txtChildInfo.setText(String.format("%s • Lv %d", childClass, childLevel));
        binding.header.txtLevel.setText(String.format("Lv %d", childLevel));

        // Format số coin với dấu phẩy (1,234)
        binding.header.txtCoin.setText(formatNumber(coin));

        // Format số XP với dấu phẩy (1,234 XP)
        binding.header.txtXP.setText(String.format("%s XP", formatNumber(xp)));

        // Load avatar từ URL bằng Glide (nếu có URL)
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_child_face) // Hiển thị placeholder khi đang load
                    .error(R.drawable.ic_child_face) // Hiển thị khi lỗi
                    .circleCrop() // Crop hình tròn
                    .into(binding.header.imgChildAvatar);
        } else {
            // Nếu không có URL, dùng icon mặc định
            binding.header.imgChildAvatar.setImageResource(R.drawable.ic_child_face);
        }
    }

    /**
     * Thiết lập biểu đồ tiến độ 7 ngày gần nhất
     */
    private void setupProgressChart() {
        // Tạo danh sách dữ liệu 7 ngày
        List<DayProgress> progressList = new ArrayList<>();
        progressList.add(new DayProgress("T2", 85));
        progressList.add(new DayProgress("T3", 80));
        progressList.add(new DayProgress("T4", 75));
        progressList.add(new DayProgress("T5", 90));
        progressList.add(new DayProgress("T6", 80));
        progressList.add(new DayProgress("T7", 70));
        progressList.add(new DayProgress("CN", 65));

        // Tạo adapter và set cho RecyclerView
        ProgressAdapter adapter = new ProgressAdapter(progressList);
        binding.progressChart.recyclerProgress.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.progressChart.recyclerProgress.setAdapter(adapter);
    }

    /**
     * Thiết lập TabLayout và ViewPager2 với custom tab
     */
    private void setupTabLayout() {
        // Setup ViewPager2 với adapter
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(requireActivity());
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
        binding = null;
    }
}
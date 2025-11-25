package com.kidsapp.ui.parent.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kidsapp.R;
import com.kidsapp.databinding.FragmentParentReportBinding;
import com.kidsapp.ui.parent.report.adapter.AchievementAdapter;
import com.kidsapp.ui.parent.report.adapter.ReportChildSelectorAdapter;
import com.kidsapp.ui.parent.report.components.WeeklyChartView;
import com.kidsapp.ui.parent.report.model.Achievement;
import com.kidsapp.ui.parent.report.model.Child;
import com.kidsapp.ui.parent.report.model.WeeklyStat;

import java.util.ArrayList;
import java.util.List;

/**
 * Parent Report Fragment - Hiển thị báo cáo chi tiết của bé
 */
public class ParentReportFragment extends Fragment {

    private FragmentParentReportBinding binding;
    
    // Data variables
    private List<Child> childList = new ArrayList<>();
    private Child selectedChild;
    private String currentFilter = "WEEK"; // WEEK, MONTH, ALL

    // UI components
    private TextView txtSelectedChild;
    private TextView imgChildAvatar;
    private TextView tabWeek, tabMonth, tabAll;
    private TextView txtHabit, txtQuiz, txtTime;
    private WeeklyChartView chartView;
    private RecyclerView recyclerAchievements;
    
    // Adapters
    private AchievementAdapter achievementAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy childId và childName từ arguments (nếu có)
        if (getArguments() != null) {
            String childId = getArguments().getString("childId");
            String childName = getArguments().getString("childName");
            
            // Tìm child trong danh sách demo
            initDemoChildren();
            if (childId != null) {
                for (Child child : childList) {
                    if (child.getId().equals(childId)) {
                        selectedChild = child;
                        break;
                    }
                }
            }
        }
        
        // Nếu không có child được chọn, chọn child đầu tiên
        if (selectedChild == null && !childList.isEmpty()) {
            selectedChild = childList.get(0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentParentReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        setupChildSelector();
        setupFilterTabs();
        setupAchievements();
        
        // Load initial data
        loadReport();
        setupAppBar();
    }

    /**
     * Khởi tạo danh sách bé DEMO
     */
    private void initDemoChildren() {
        childList.clear();
        childList.add(new Child("1", "Hồ Hữu Huy", 3, 1200, "👦"));
        childList.add(new Child("2", "Linh", 2, 900, "👧"));
        childList.add(new Child("3", "Tuấn", 4, 1500, "👦"));
    }

    /**
     * Khởi tạo các view components
     */
    private void initViews() {
        // Child selector views
        View childSelectorLayout = binding.getRoot().findViewById(R.id.childSelector);
        txtSelectedChild = childSelectorLayout.findViewById(R.id.txtSelectedChild);
        imgChildAvatar = childSelectorLayout.findViewById(R.id.imgChildAvatar);

        // Filter tabs
        View filterTabsLayout = binding.getRoot().findViewById(R.id.filterTabs);
        tabWeek = filterTabsLayout.findViewById(R.id.tabWeek);
        tabMonth = filterTabsLayout.findViewById(R.id.tabMonth);
        tabAll = filterTabsLayout.findViewById(R.id.tabAll);

        // Info cards
        View infoCardsLayout = binding.getRoot().findViewById(R.id.infoCards);
        txtHabit = infoCardsLayout.findViewById(R.id.txtHabit);
        txtQuiz = infoCardsLayout.findViewById(R.id.txtQuiz);
        txtTime = infoCardsLayout.findViewById(R.id.txtTime);

        // Chart
        chartView = binding.getRoot().findViewById(R.id.chartView);

        // Achievements RecyclerView
        recyclerAchievements = binding.getRoot().findViewById(R.id.recyclerAchievements);
    }

    /**
     * Setup child selector với bottom sheet
     */
    private void setupChildSelector() {
        if (selectedChild != null) {
            txtSelectedChild.setText(selectedChild.getName() + " – " + selectedChild.getLevelText());
            imgChildAvatar.setText(selectedChild.getAvatar());
        }

        View childSelectorLayout = binding.getRoot().findViewById(R.id.childSelector);
        childSelectorLayout.setOnClickListener(v -> showChildBottomSheet());
    }

    /**
     * Hiển thị BottomSheet chọn bé
     */
    private void showChildBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottomsheet_child_selector, null);
        
        RecyclerView recyclerChildList = bottomSheetView.findViewById(R.id.recyclerChildList);
        
        ReportChildSelectorAdapter adapter = new ReportChildSelectorAdapter(child -> {
            // Cập nhật selected child
            selectedChild = child;
            txtSelectedChild.setText(child.getName() + " – " + child.getLevelText());
            imgChildAvatar.setText(child.getAvatar());
            
            // Đóng bottom sheet
            bottomSheetDialog.dismiss();
            
            // Tải lại dữ liệu
            loadReport();
        });
        
        adapter.setChildList(childList);
        recyclerChildList.setAdapter(adapter);
        
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    /**
     * Setup các tab filter: Tuần này - Tháng này - Tất cả
     */
    private void setupFilterTabs() {
        tabWeek.setOnClickListener(v -> {
            setActiveTab("WEEK");
            loadReport();
        });

        tabMonth.setOnClickListener(v -> {
            setActiveTab("MONTH");
            loadReport();
        });

        tabAll.setOnClickListener(v -> {
            setActiveTab("ALL");
            loadReport();
        });
        
        // Set initial active state
        setActiveTab("WEEK");
    }

    /**
     * Đổi trạng thái active cho tab
     */
    private void setActiveTab(String filter) {
        currentFilter = filter;
        
        // Reset all tabs
        tabWeek.setBackgroundResource(android.R.color.transparent);
        tabWeek.setTextColor(getResources().getColor(R.color.text_secondary));
        
        tabMonth.setBackgroundResource(android.R.color.transparent);
        tabMonth.setTextColor(getResources().getColor(R.color.text_secondary));
        
        tabAll.setBackgroundResource(android.R.color.transparent);
        tabAll.setTextColor(getResources().getColor(R.color.text_secondary));
        
        // Set active tab
        switch (filter) {
            case "WEEK":
                tabWeek.setBackgroundResource(R.drawable.bg_tab_active);
                tabWeek.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case "MONTH":
                tabMonth.setBackgroundResource(R.drawable.bg_tab_active);
                tabMonth.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case "ALL":
                tabAll.setBackgroundResource(R.drawable.bg_tab_active);
                tabAll.setTextColor(getResources().getColor(android.R.color.white));
                break;
        }
    }

    /**
     * Setup RecyclerView cho achievements
     */
    private void setupAchievements() {
        achievementAdapter = new AchievementAdapter();
        recyclerAchievements.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        
        // Thêm spacing giữa các item
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_8);
        recyclerAchievements.addItemDecoration(
                new com.kidsapp.ui.parent.report.components.GridSpacingItemDecoration(3, spacingInPixels, false)
        );
        
        recyclerAchievements.setAdapter(achievementAdapter);
    }
    private void setupAppBar() {
        // Sự kiện click nút Back - gọi onBackPressed của Activity để xử lý logic
        binding.appbar.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }
    /**
     * Load báo cáo theo filter hiện tại
     */
    private void loadReport() {
        if (selectedChild == null) {
            Toast.makeText(requireContext(), "Chưa chọn bé", Toast.LENGTH_SHORT).show();
            return;
        }
        
        switch (currentFilter) {
            case "WEEK":
                loadWeeklyReport(selectedChild);
                break;
            case "MONTH":
                loadMonthlyReport(selectedChild);
                break;
            case "ALL":
                loadAllReport(selectedChild);
                break;
        }
    }

    /**
     * Load dữ liệu báo cáo tuần này (DEMO)
     */
    private void loadWeeklyReport(Child child) {
        // Info cards
        txtHabit.setText("24");
        txtQuiz.setText("18");
        txtTime.setText("3.2h");
        
        // Chart data (7 ngày)
        List<WeeklyStat> weeklyStats = new ArrayList<>();
        weeklyStats.add(new WeeklyStat("T2", 4, 3));
        weeklyStats.add(new WeeklyStat("T3", 5, 2));
        weeklyStats.add(new WeeklyStat("T4", 3, 4));
        weeklyStats.add(new WeeklyStat("T5", 6, 3));
        weeklyStats.add(new WeeklyStat("T6", 2, 2));
        weeklyStats.add(new WeeklyStat("T7", 3, 3));
        weeklyStats.add(new WeeklyStat("CN", 1, 1));
        chartView.setData(weeklyStats);
        
        // Achievements
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new Achievement("1", "Siêu sao", "⭐", 5));
        achievements.add(new Achievement("2", "Nỗ lực", "💪", 8));
        achievements.add(new Achievement("3", "Kiên trì", "🔥", 12));
        achievements.add(new Achievement("4", "Tia chớp", "⚡", 6));
        achievements.add(new Achievement("5", "Ngôi sao vàng", "🌟", 4));
        achievements.add(new Achievement("6", "MVP", "🏆", 3));
        achievementAdapter.setAchievementList(achievements);
    }

    /**
     * Load dữ liệu báo cáo tháng này (DEMO)
     */
    private void loadMonthlyReport(Child child) {
        // Info cards
        txtHabit.setText("96");
        txtQuiz.setText("72");
        txtTime.setText("12.8h");
        
        // Chart data (4 tuần)
        List<WeeklyStat> monthlyStats = new ArrayList<>();
        monthlyStats.add(new WeeklyStat("T1", 20, 15));
        monthlyStats.add(new WeeklyStat("T2", 24, 18));
        monthlyStats.add(new WeeklyStat("T3", 28, 20));
        monthlyStats.add(new WeeklyStat("T4", 24, 19));
        chartView.setData(monthlyStats);
        
        // Achievements
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new Achievement("1", "Siêu sao", "⭐", 20));
        achievements.add(new Achievement("2", "Nỗ lực", "💪", 32));
        achievements.add(new Achievement("3", "Kiên trì", "🔥", 48));
        achievements.add(new Achievement("4", "Tia chớp", "⚡", 24));
        achievements.add(new Achievement("5", "Ngôi sao vàng", "🌟", 16));
        achievements.add(new Achievement("6", "MVP", "🏆", 12));
        achievementAdapter.setAchievementList(achievements);
    }

    /**
     * Load dữ liệu báo cáo tất cả (DEMO)
     */
    private void loadAllReport(Child child) {
        // Info cards
        txtHabit.setText("288");
        txtQuiz.setText("216");
        txtTime.setText("38.4h");
        
        // Chart data (3 tháng)
        List<WeeklyStat> allStats = new ArrayList<>();
        allStats.add(new WeeklyStat("T1", 80, 60));
        allStats.add(new WeeklyStat("T2", 88, 68));
        allStats.add(new WeeklyStat("T3", 96, 72));
        allStats.add(new WeeklyStat("T4", 24, 16));
        chartView.setData(allStats);
        
        // Achievements
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new Achievement("1", "Siêu sao", "⭐", 60));
        achievements.add(new Achievement("2", "Nỗ lực", "💪", 96));
        achievements.add(new Achievement("3", "Kiên trì", "🔥", 144));
        achievements.add(new Achievement("4", "Tia chớp", "⚡", 72));
        achievements.add(new Achievement("5", "Ngôi sao vàng", "🌟", 48));
        achievements.add(new Achievement("6", "MVP", "🏆", 36));
        achievementAdapter.setAchievementList(achievements);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

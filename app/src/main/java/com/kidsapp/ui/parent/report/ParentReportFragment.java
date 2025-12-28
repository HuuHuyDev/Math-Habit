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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kidsapp.R;
import com.kidsapp.data.api.ApiService;
import com.kidsapp.data.api.RetrofitClient;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.databinding.FragmentParentReportBinding;
import com.kidsapp.ui.components.LoadingDialog;
import com.kidsapp.ui.parent.report.adapter.AchievementAdapter;
import com.kidsapp.ui.parent.report.adapter.ReportChildSelectorAdapter;
import com.kidsapp.ui.parent.report.components.WeeklyChartView;
import com.kidsapp.ui.parent.report.model.Achievement;
import com.kidsapp.ui.parent.report.model.Child;
import com.kidsapp.ui.parent.report.model.WeeklyStat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Parent Report Fragment - Hiển thị báo cáo chi tiết của bé
 */
public class ParentReportFragment extends Fragment {

    private FragmentParentReportBinding binding;
    private ApiService apiService;
    private LoadingDialog loadingDialog;
    
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
        
        // Init API service
        SharedPref sharedPref = new SharedPref(requireContext());
        apiService = RetrofitClient.getInstance(sharedPref).getApiService();

        // Lấy childId và childName từ arguments (nếu có)
        if (getArguments() != null) {
            String childId = getArguments().getString("childId");
            String childName = getArguments().getString("childName");
            
            if (childId != null && childName != null) {
                selectedChild = new Child(childId, childName, 1, 0, "👦");
            }
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

        loadingDialog = new LoadingDialog(requireContext());
        
        initViews();
        setupFilterTabs();
        setupAchievements();
        setupAppBar();
        
        // Load children list first, then load report
        showLoading();
        loadChildrenList();
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
     * Load danh sách children từ API
     */
    private void loadChildrenList() {
        apiService.getParentChildren().enqueue(new Callback<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> call,
                                   Response<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> response) {
                if (!isAdded()) return;
                
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    childList.clear();
                    for (ApiService.ChildResponse child : response.body().data) {
                        String avatar = (child.gender != null && child.gender) ? "👦" : "👧";
                        if (child.avatarUrl != null && !child.avatarUrl.isEmpty() && !child.avatarUrl.startsWith("http")) {
                            avatar = child.avatarUrl;
                        }
                        int level = child.currentLevel != null ? child.currentLevel : 1;
                        int xp = child.totalXp != null ? child.totalXp : 0;
                        childList.add(new Child(child.id, child.name, level, xp, avatar));
                    }
                    
                    // Nếu chưa có selected child, chọn child đầu tiên
                    if (selectedChild == null && !childList.isEmpty()) {
                        selectedChild = childList.get(0);
                    } else if (selectedChild != null) {
                        // Cập nhật thông tin selected child từ API
                        for (Child c : childList) {
                            if (c.getId().equals(selectedChild.getId())) {
                                selectedChild = c;
                                break;
                            }
                        }
                    }
                    
                    setupChildSelector();
                    loadReport();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponseWrapper<List<ApiService.ChildResponse>>> call, Throwable t) {
                if (!isAdded()) return;
                hideLoading();
                Toast.makeText(requireContext(), "Không thể tải danh sách bé", Toast.LENGTH_SHORT).show();
            }
        });
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
            selectedChild = child;
            txtSelectedChild.setText(child.getName() + " – " + child.getLevelText());
            imgChildAvatar.setText(child.getAvatar());
            bottomSheetDialog.dismiss();
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
        
        setActiveTab("WEEK");
    }

    /**
     * Đổi trạng thái active cho tab
     */
    private void setActiveTab(String filter) {
        currentFilter = filter;
        
        tabWeek.setBackgroundResource(android.R.color.transparent);
        tabWeek.setTextColor(getResources().getColor(R.color.text_secondary));
        
        tabMonth.setBackgroundResource(android.R.color.transparent);
        tabMonth.setTextColor(getResources().getColor(R.color.text_secondary));
        
        tabAll.setBackgroundResource(android.R.color.transparent);
        tabAll.setTextColor(getResources().getColor(R.color.text_secondary));
        
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
        
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_8);
        recyclerAchievements.addItemDecoration(
                new com.kidsapp.ui.parent.report.components.GridSpacingItemDecoration(3, spacingInPixels, false)
        );
        
        recyclerAchievements.setAdapter(achievementAdapter);
    }
    
    private void setupAppBar() {
        binding.appbar.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }
    
    /**
     * Load báo cáo từ API
     */
    private void loadReport() {
        if (selectedChild == null) {
            return;
        }
        
        apiService.getDetailReport(selectedChild.getId(), currentFilter)
                .enqueue(new Callback<ApiService.ApiResponseWrapper<ApiService.DetailReportResponse>>() {
                    @Override
                    public void onResponse(Call<ApiService.ApiResponseWrapper<ApiService.DetailReportResponse>> call,
                                           Response<ApiService.ApiResponseWrapper<ApiService.DetailReportResponse>> response) {
                        if (!isAdded()) return;
                        
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            updateUI(response.body().data);
                        } else {
                            showEmptyState();
                        }
                        hideLoading();
                    }

                    @Override
                    public void onFailure(Call<ApiService.ApiResponseWrapper<ApiService.DetailReportResponse>> call, Throwable t) {
                        if (!isAdded()) return;
                        hideLoading();
                        showEmptyState();
                    }
                });
    }
    
    /**
     * Cập nhật UI với dữ liệu từ API
     */
    private void updateUI(ApiService.DetailReportResponse data) {
        // Info cards
        txtHabit.setText(String.valueOf(data.totalHabits));
        txtQuiz.setText(String.valueOf(data.totalExercises));
        txtTime.setText(data.totalTime != null ? data.totalTime : "0m");
        
        // Chart data
        if (data.chartData != null && !data.chartData.isEmpty()) {
            List<WeeklyStat> stats = new ArrayList<>();
            for (ApiService.ChartData chart : data.chartData) {
                stats.add(new WeeklyStat(chart.label, chart.habitCount, chart.exerciseCount));
            }
            chartView.setData(stats);
        }
        
        // Achievements
        if (data.achievements != null && !data.achievements.isEmpty()) {
            List<Achievement> achievements = new ArrayList<>();
            for (ApiService.AchievementData ach : data.achievements) {
                achievements.add(new Achievement(ach.id, ach.name, ach.icon, ach.count));
            }
            achievementAdapter.setAchievementList(achievements);
        } else {
            achievementAdapter.setAchievementList(new ArrayList<>());
        }
    }
    
    /**
     * Hiển thị trạng thái trống
     */
    private void showEmptyState() {
        txtHabit.setText("0");
        txtQuiz.setText("0");
        txtTime.setText("0m");
        chartView.setData(new ArrayList<>());
        achievementAdapter.setAchievementList(new ArrayList<>());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideLoading();
        loadingDialog = null;
        binding = null;
    }
}

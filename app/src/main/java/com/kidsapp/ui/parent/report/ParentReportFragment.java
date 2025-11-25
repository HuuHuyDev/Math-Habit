package com.kidsapp.ui.parent.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.kidsapp.R;
import com.kidsapp.databinding.FragmentParentReportBinding;

/**
 * Parent Report Fragment - Hiển thị báo cáo chi tiết của bé
 */
public class ParentReportFragment extends Fragment {

    private FragmentParentReportBinding binding;
    private String childId;
    private String childName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy childId và childName từ arguments
        if (getArguments() != null) {
            childId = getArguments().getString("childId");
            childName = getArguments().getString("childName");
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

        setupAppBar();
        loadChildReport();
        setupTabs();
    }

    /**
     * Setup AppBar với nút back
     */
    private void setupAppBar() {
        // Set title với tên bé
        if (childName != null && !childName.isEmpty()) {
            // Nếu có TextView title trong layout, set text
            // binding.txtTitle.setText(childName + " - Báo cáo");
        }

        // Setup nút back (nếu có trong layout)
        // binding.btnBack.setOnClickListener(v -> {
        //     Navigation.findNavController(v).popBackStack();
        // });
    }

    /**
     * Load dữ liệu báo cáo của bé từ Firebase/API
     */
    private void loadChildReport() {
        if (childId == null || childId.isEmpty()) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin bé", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Load dữ liệu báo cáo từ Firebase/API
        // Ví dụ:
        // FirebaseFirestore db = FirebaseFirestore.getInstance();
        // db.collection("children")
        //     .document(childId)
        //     .collection("reports")
        //     .orderBy("date", Query.Direction.DESCENDING)
        //     .limit(30) // Lấy 30 ngày gần nhất
        //     .get()
        //     .addOnSuccessListener(queryDocumentSnapshots -> {
        //         // Parse dữ liệu và hiển thị
        //         displayReportData(queryDocumentSnapshots);
        //     })
        //     .addOnFailureListener(e -> {
        //         Toast.makeText(requireContext(), "Lỗi tải báo cáo", Toast.LENGTH_SHORT).show();
        //     });

        // Tạm thời hiển thị thông báo
        Toast.makeText(requireContext(),
                "Đang tải báo cáo của " + (childName != null ? childName : "bé"),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Setup các tab: Tuần này, Tháng này, Tất cả
     */
    private void setupTabs() {
        // TODO: Setup TabLayout nếu có trong layout
        // Ví dụ:
        // binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tuần này"));
        // binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tháng này"));
        // binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tất cả"));

        // binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
        //     @Override
        //     public void onTabSelected(TabLayout.Tab tab) {
        //         loadReportByPeriod(tab.getPosition());
        //     }
        //     @Override
        //     public void onTabUnselected(TabLayout.Tab tab) {}
        //     @Override
        //     public void onTabReselected(TabLayout.Tab tab) {}
        // });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
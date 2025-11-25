package com.kidsapp.ui.parent.task_plan;

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
import com.kidsapp.databinding.FragmentWeeklyPlanBinding;

/**
 * Weekly Plan Fragment - Chỉnh mục tiêu tuần của bé
 */
public class WeeklyPlanFragment extends Fragment {
    private FragmentWeeklyPlanBinding binding;
    private String childId;
    private String childName;
    private int childLevel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy childId, childName, childLevel từ arguments
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
        binding = FragmentWeeklyPlanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupAppBar();
        loadCurrentGoals();

        // Hiển thị thông tin nếu có childId
        if (childId != null && !childId.isEmpty()) {
            String message = "Chỉnh mục tiêu tuần của: " +
                    (childName != null ? childName : "bé") +
                    " (Lớp " + childLevel + ")";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Setup AppBar với nút back
     */
    private void setupAppBar() {
        // TODO: Setup nút back trong layout nếu có
        // binding.btnBack.setOnClickListener(v -> {
        //     Navigation.findNavController(v).popBackStack();
        // });

        // TODO: Set title
        // if (childName != null && !childName.isEmpty()) {
        //     binding.txtTitle.setText("Mục tiêu tuần - " + childName);
        // } else {
        //     binding.txtTitle.setText("Mục tiêu tuần");
        // }
    }

    /**
     * Load mục tiêu hiện tại của bé từ Firebase/API
     */
    private void loadCurrentGoals() {
        if (childId == null || childId.isEmpty()) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin bé", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Load dữ liệu mục tiêu từ Firebase/API
        // Ví dụ:
        // FirebaseFirestore db = FirebaseFirestore.getInstance();
        // db.collection("children")
        //     .document(childId)
        //     .collection("weeklyGoals")
        //     .document("current")
        //     .get()
        //     .addOnSuccessListener(documentSnapshot -> {
        //         if (documentSnapshot.exists()) {
        //             WeeklyGoal goal = documentSnapshot.toObject(WeeklyGoal.class);
        //             displayGoal(goal);
        //         }
        //     })
        //     .addOnFailureListener(e -> {
        //         Toast.makeText(requireContext(), "Lỗi tải mục tiêu", Toast.LENGTH_SHORT).show();
        //     });
    }

    /**
     * Lưu mục tiêu mới
     */
    private void saveWeeklyGoal() {
        if (childId == null || childId.isEmpty()) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin bé", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Validate input và lưu vào Firebase/API
        // Ví dụ:
        // Map<String, Object> goalData = new HashMap<>();
        // goalData.put("exerciseGoal", exerciseGoal);
        // goalData.put("houseworkGoal", houseworkGoal);
        // goalData.put("updatedAt", FieldValue.serverTimestamp());
        //
        // FirebaseFirestore.getInstance()
        //     .collection("children")
        //     .document(childId)
        //     .collection("weeklyGoals")
        //     .document("current")
        //     .set(goalData)
        //     .addOnSuccessListener(aVoid -> {
        //         Toast.makeText(requireContext(), "Đã lưu mục tiêu", Toast.LENGTH_SHORT).show();
        //         Navigation.findNavController(requireView()).popBackStack();
        //     })
        //     .addOnFailureListener(e -> {
        //         Toast.makeText(requireContext(), "Lỗi lưu mục tiêu", Toast.LENGTH_SHORT).show();
        //     });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


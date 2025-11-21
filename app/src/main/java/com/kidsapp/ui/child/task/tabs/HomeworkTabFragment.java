package com.kidsapp.ui.child.task.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.data.model.HouseworkTask;
import com.kidsapp.databinding.FragmentHomeworkTabBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab "Việc nhà"
 * - Hiển thị danh sách công việc nhà với checkbox tick chọn.
 * - Nút "Hoàn thành" ở dưới cùng để đánh dấu các công việc đã chọn là hoàn thành.
 */
public class HomeworkTabFragment extends Fragment {

    private FragmentHomeworkTabBinding binding;
    private HouseworkAdapter adapter;
    private final List<HouseworkTask> houseworkTasks = new ArrayList<>();

    public HomeworkTabFragment() {
        // Required empty public constructor
    }

    public static HomeworkTabFragment newInstance(String param1, String param2) {
        return new HomeworkTabFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeworkTabBinding.inflate(inflater, container, false);
        setupRecyclerView();
        setupCompleteButton();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        houseworkTasks.clear();
        houseworkTasks.addAll(createSampleTasks());

        adapter = new HouseworkAdapter(houseworkTasks, this::updateCompleteButtonState);

        binding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewTasks.setAdapter(adapter);

        // Khởi tạo trạng thái nút "Hoàn thành"
        updateCompleteButtonState(0);
    }

    private void setupCompleteButton() {
        binding.btnComplete.setOnClickListener(v -> {
            List<HouseworkTask> selectedTasks = adapter.getSelectedTasks();
            int completedCount = selectedTasks.size();

            if (completedCount == 0) {
                return;
            }

            // Đánh dấu các công việc đã chọn là hoàn thành
            for (HouseworkTask task : selectedTasks) {
                task.setCompleted(true);
                task.setSelected(false);
            }

            adapter.notifyDataSetChanged();

            // Cập nhật lại trạng thái nút (vì không còn task nào đang được chọn)
            updateCompleteButtonState(0);

            // Hiển thị Toast thông báo
            Toast.makeText(
                    requireContext(),
                    "Bạn đã hoàn thành " + completedCount + " công việc!",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    /**
     * Bật/tắt nút "Hoàn thành" dựa trên số lượng task được chọn.
     */
    private void updateCompleteButtonState(int selectedCount) {
        boolean enabled = selectedCount > 0;
        binding.btnComplete.setEnabled(enabled);
        binding.btnComplete.setAlpha(enabled ? 1.0f : 0.5f);
    }

    /**
     * Tạo danh sách sample 3–5 công việc nhà.
     */
    private List<HouseworkTask> createSampleTasks() {
        List<HouseworkTask> tasks = new ArrayList<>();
        tasks.add(new HouseworkTask("Công việc 1: Dọn dẹp phòng", "Buổi sáng, sau khi thức dậy"));
        tasks.add(new HouseworkTask("Công việc 2: Rửa bát", "Sau bữa trưa hoặc bữa tối"));
        tasks.add(new HouseworkTask("Công việc 3: Quét nhà", "Buổi chiều, trước giờ chơi"));
        tasks.add(new HouseworkTask("Công việc 4: Tưới cây", "Buổi sáng, trước khi đi học"));
        tasks.add(new HouseworkTask("Công việc 5: Gấp quần áo", "Buổi tối, sau khi tắm"));
        return tasks;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

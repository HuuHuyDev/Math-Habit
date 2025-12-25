package com.kidsapp.ui.parent.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kidsapp.R;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.databinding.FragmentParentChatListBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị danh sách chat với các con
 */
public class ParentChatListFragment extends Fragment implements ChildChatAdapter.OnChildChatClickListener {

    private FragmentParentChatListBinding binding;
    private ChildChatAdapter adapter;
    private SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentParentChatListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        sharedPref = new SharedPref(requireContext());
        setupRecyclerView();
        loadChildren();
    }

    private void setupRecyclerView() {
        adapter = new ChildChatAdapter(this);
        binding.rvChildren.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChildren.setAdapter(adapter);
    }

    private void loadChildren() {
        // TODO: Load từ API - hiện tại dùng mock data
        List<ChildChatItem> children = new ArrayList<>();
        
        children.add(new ChildChatItem(
                "1", "Hồ Hữu Huy", null,
                "Con làm xong bài rồi ạ! 🎉", "10:30",
                2, true, 3
        ));
        children.add(new ChildChatItem(
                "2", "Linh", null,
                "Dạ con hiểu rồi ạ", "Hôm qua",
                0, false, 2
        ));

        adapter.setChildren(children);
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean isEmpty = adapter.isEmpty();
        binding.layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.rvChildren.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onChildChatClick(ChildChatItem child) {
        // Mở màn hình chat với con sử dụng Navigation
        Bundle args = new Bundle();
        args.putString("child_id", child.getId());
        args.putString("child_name", child.getName());

        try {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_chatHub_to_chatRoom, args);
        } catch (Exception e) {
            // Fallback nếu không tìm thấy action
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

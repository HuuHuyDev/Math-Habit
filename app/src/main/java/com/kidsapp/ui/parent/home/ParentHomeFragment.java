package com.kidsapp.ui.parent.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kidsapp.R;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.data.model.ActivityLog;
import com.kidsapp.databinding.FragmentParentHomeBinding;
import com.kidsapp.ui.parent.home.adapter.ActivityLogAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Parent Home Fragment
 */
public class ParentHomeFragment extends Fragment {
    private FragmentParentHomeBinding binding;
    private ActivityLogAdapter activityLogAdapter;
    private SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentParentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        sharedPref = new SharedPref(requireContext());
        setupRecyclerView();
        setupData();
    }

    private void setupRecyclerView() {
        activityLogAdapter = new ActivityLogAdapter(new ArrayList<>());
        binding.rvActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvActivities.setAdapter(activityLogAdapter);
    }

    private void setupData() {
        // Set greeting
        String userName = sharedPref.getUserName();
        if (userName != null) {
            binding.tvGreeting.setText(getString(R.string.hello_parent, userName));
        }
        
        // Set summary values (mock data for now)
        binding.tvSummary1Value.setText("12");
        binding.tvSummary2Value.setText("450");
        
        // Load activity logs (mock data)
        List<ActivityLog> activities = getMockActivities();
        activityLogAdapter.updateList(activities);
    }

    private List<ActivityLog> getMockActivities() {
        List<ActivityLog> activities = new ArrayList<>();
        activities.add(new ActivityLog("1", "1", "Bé An", "đã hoàn thành nhiệm vụ Toán học", 
            50, null, null, "2024-01-01 10:00:00"));
        activities.add(new ActivityLog("2", "1", "Bé An", "đã nhận được huy hiệu", 
            100, null, null, "2024-01-01 09:00:00"));
        return activities;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


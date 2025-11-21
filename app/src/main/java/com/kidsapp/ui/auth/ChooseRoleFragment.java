package com.kidsapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.kidsapp.R;
import com.kidsapp.databinding.FragmentChooseRoleBinding;
import com.kidsapp.ui.child.main.ChildMainActivity;
import com.kidsapp.ui.parent.main.ParentMainActivity;
import com.kidsapp.utils.Constants;

/**
 * Choose Role Fragment - Select Parent or Child role
 */
public class ChooseRoleFragment extends Fragment {
    private FragmentChooseRoleBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChooseRoleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Parent card click
        binding.cardParent.setOnClickListener(v -> {
            navigateToParent();
        });
        
        // Child card click
        binding.cardChild.setOnClickListener(v -> {
            navigateToChild();
        });
    }

    private void navigateToParent() {
        Intent intent = new Intent(getActivity(), ParentMainActivity.class);
        intent.putExtra("role", Constants.ROLE_PARENT);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void navigateToChild() {
        Intent intent = new Intent(getActivity(), ChildMainActivity.class);
        intent.putExtra("role", Constants.ROLE_CHILD);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


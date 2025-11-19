package com.kidsapp.ui.child.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.kidsapp.R;
import com.kidsapp.databinding.FragmentProfileChildBinding;

/**
 * Child Profile Fragment
 */
public class ChildProfileFragment extends Fragment {
    private FragmentProfileChildBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileChildBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


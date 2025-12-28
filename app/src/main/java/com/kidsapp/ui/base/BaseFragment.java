package com.kidsapp.ui.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kidsapp.ui.components.LoadingDialog;

/**
 * Base Fragment với loading dialog dùng chung
 */
public abstract class BaseFragment extends Fragment {

    private LoadingDialog loadingDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(requireContext());
    }

    protected void showLoading() {
        if (loadingDialog != null) {
            loadingDialog.show();
        }
    }

    protected void showLoading(String message) {
        if (loadingDialog != null) {
            loadingDialog.show(message);
        }
    }

    protected void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    protected boolean isLoadingShowing() {
        return loadingDialog != null && loadingDialog.isShowing();
    }

    @Override
    public void onDestroyView() {
        hideLoading();
        loadingDialog = null;
        super.onDestroyView();
    }
}

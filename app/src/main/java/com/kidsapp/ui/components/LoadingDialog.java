package com.kidsapp.ui.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kidsapp.R;

/**
 * Loading dialog dùng chung cho toàn app
 * Hiển thị khi chuyển trang hoặc gọi API
 */
public class LoadingDialog {

    private Dialog dialog;
    private TextView txtMessage;

    public LoadingDialog(@NonNull Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        
        txtMessage = dialog.findViewById(R.id.txtMessage);
    }

    public void show() {
        show("Đang tải...");
    }

    public void show(String message) {
        if (txtMessage != null) {
            txtMessage.setText(message);
        }
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setMessage(String message) {
        if (txtMessage != null) {
            txtMessage.setText(message);
        }
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }
}

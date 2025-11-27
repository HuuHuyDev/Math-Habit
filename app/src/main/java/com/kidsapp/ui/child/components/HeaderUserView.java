package com.kidsapp.ui.child.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.kidsapp.R;

public class HeaderUserView extends ConstraintLayout {
    private ImageView imgAvatar;
    private TextView txtUserName;
    private View btnNotification;

    public HeaderUserView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init(ctx);
    }

    private void init(Context ctx) {
        LayoutInflater.from(ctx).inflate(R.layout.view_header_user, this, true);

        imgAvatar = findViewById(R.id.imgAvatar);
        txtUserName = findViewById(R.id.txtUserName);
        btnNotification = findViewById(R.id.btnNotification);
    }

    public void setUserName(String name) {
        txtUserName.setText(name);
    }

    public void setAvatar(int imgRes) {
        imgAvatar.setImageResource(imgRes);
    }

    public void setNotificationClick(OnClickListener listener) {
        btnNotification.setOnClickListener(listener);
    }

    /**
     * Xử lý sự kiện click vào avatar
     * @param listener Listener để xử lý khi click vào avatar
     */
    public void setAvatarClick(OnClickListener listener) {
        imgAvatar.setOnClickListener(listener);
    }
}

package com.kidsapp.ui.child.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidsapp.R;

/**
 * Header component chung cho các trang Child
 * Có thể tái sử dụng ở nhiều Fragment khác nhau
 */
public class HeaderUserView extends LinearLayout {
    private ImageView imgAvatar;
    private TextView txtUserName;
    private FrameLayout btnNotification;
    private TextView txtNotificationBadge;

    public HeaderUserView(Context context) {
        super(context);
        init(context);
    }

    public HeaderUserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HeaderUserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_header_user, this, true);

        imgAvatar = findViewById(R.id.imgAvatar);
        txtUserName = findViewById(R.id.txtUserName);
        btnNotification = findViewById(R.id.btnNotification);
        txtNotificationBadge = findViewById(R.id.txtNotificationBadge);
    }

    /**
     * Set tên người dùng
     */
    public void setUserName(String name) {
        txtUserName.setText(name);
    }

    /**
     * Set avatar từ resource
     */
    public void setAvatar(int imgRes) {
        imgAvatar.setImageResource(imgRes);
    }

    /**
     * Set click listener cho avatar
     */
    public void setAvatarClick(OnClickListener listener) {
        imgAvatar.setOnClickListener(listener);
    }

    /**
     * Set click listener cho nút thông báo
     */
    public void setNotificationClick(OnClickListener listener) {
        btnNotification.setOnClickListener(listener);
    }

    /**
     * Set số lượng thông báo chưa đọc
     * @param count Số thông báo (0 = ẩn badge)
     */
    public void setNotificationCount(int count) {
        if (count > 0) {
            txtNotificationBadge.setVisibility(View.VISIBLE);
            txtNotificationBadge.setText(count > 9 ? "9+" : String.valueOf(count));
        } else {
            txtNotificationBadge.setVisibility(View.GONE);
        }
    }
}

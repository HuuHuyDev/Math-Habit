package com.kidsapp.data;

import com.kidsapp.ui.parent.home.model.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Fake repository cho thông báo demo
 */
public class FakeNotificationRepository {

    public static List<Notification> getDemoNotifications() {
        List<Notification> notifications = new ArrayList<>();

        notifications.add(new Notification(
                "1",
                "Huy",
                "👦",
                "Đánh răng sáng",
                "habit",
                "5 phút trước",
                false
        ));

        notifications.add(new Notification(
                "2",
                "Linh",
                "👧",
                "Quiz Cộng level 2",
                "quiz",
                "15 phút trước",
                false
        ));

        notifications.add(new Notification(
                "3",
                "Huy",
                "👦",
                "Đọc sách 15 phút",
                "habit",
                "1 giờ trước",
                true
        ));

        notifications.add(new Notification(
                "4",
                "Tuấn",
                "👦",
                "Rửa bát sau bữa ăn",
                "habit",
                "2 giờ trước",
                true
        ));

        notifications.add(new Notification(
                "5",
                "Linh",
                "👧",
                "Quiz Trừ level 1",
                "quiz",
                "3 giờ trước",
                true
        ));

        notifications.add(new Notification(
                "6",
                "Huy",
                "👦",
                "Gấp quần áo",
                "habit",
                "Hôm qua",
                true
        ));

        return notifications;
    }
}

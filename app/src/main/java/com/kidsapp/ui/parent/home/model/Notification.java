package com.kidsapp.ui.parent.home.model;

/**
 * Model cho thông báo
 */
public class Notification {
    private String id;
    private String childName;
    private String childAvatar;
    private String taskTitle;
    private String taskType; // "habit" hoặc "quiz"
    private String time; // "5 phút trước", "1 giờ trước"
    private boolean isRead;

    public Notification(String id, String childName, String childAvatar, String taskTitle,
                       String taskType, String time, boolean isRead) {
        this.id = id;
        this.childName = childName;
        this.childAvatar = childAvatar;
        this.taskTitle = taskTitle;
        this.taskType = taskType;
        this.time = time;
        this.isRead = isRead;
    }

    public String getId() {
        return id;
    }

    public String getChildName() {
        return childName;
    }

    public String getChildAvatar() {
        return childAvatar;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getTime() {
        return time;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getMessage() {
        return childName + " đã hoàn thành \"" + taskTitle + "\"";
    }

    public boolean isHabit() {
        return "habit".equalsIgnoreCase(taskType);
    }
}

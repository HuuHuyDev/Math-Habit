package com.kidsapp.ui.parent.child.detail.tabs.badge;

/**
 * Model class cho huy hiệu
 */
public class BadgeItem {
    private String id;
    private String name;
    private boolean isUnlocked;
    private int iconRes;

    public BadgeItem(String id, String name, boolean isUnlocked, int iconRes) {
        this.id = id;
        this.name = name;
        this.isUnlocked = isUnlocked;
        this.iconRes = iconRes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
}

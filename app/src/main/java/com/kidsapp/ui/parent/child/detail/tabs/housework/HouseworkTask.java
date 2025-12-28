package com.kidsapp.ui.parent.child.detail.tabs.housework;

/**
 * Model class cho HABIT task
 */
public class HouseworkTask {
    private String id;
    private String title;
    private boolean isCompleted;
    private int iconRes;
    private String status; // PENDING, SUBMITTED, COMPLETED, REJECTED
    private String proofUrl;
    private boolean isSubmitted;

    public HouseworkTask(String id, String title, boolean isCompleted, int iconRes) {
        this.id = id;
        this.title = title;
        this.isCompleted = isCompleted;
        this.iconRes = iconRes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProofUrl() {
        return proofUrl;
    }

    public void setProofUrl(String proofUrl) {
        this.proofUrl = proofUrl;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public void setSubmitted(boolean submitted) {
        isSubmitted = submitted;
    }
    
    /**
     * Check if this task needs approval from parent
     */
    public boolean needsApproval() {
        return "SUBMITTED".equalsIgnoreCase(status);
    }
}

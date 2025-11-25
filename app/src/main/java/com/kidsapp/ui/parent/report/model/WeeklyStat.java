package com.kidsapp.ui.parent.report.model;

/**
 * Model class cho thống kê hàng tuần (mỗi cột trong biểu đồ)
 */
public class WeeklyStat {
    private String label; // T2, T3, T4, T5, T6, T7, CN
    private int habitCount; // Số thói quen hoàn thành
    private int quizCount; // Số bài tập hoàn thành

    public WeeklyStat(String label, int habitCount, int quizCount) {
        this.label = label;
        this.habitCount = habitCount;
        this.quizCount = quizCount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getHabitCount() {
        return habitCount;
    }

    public void setHabitCount(int habitCount) {
        this.habitCount = habitCount;
    }

    public int getQuizCount() {
        return quizCount;
    }

    public void setQuizCount(int quizCount) {
        this.quizCount = quizCount;
    }

    public int getTotalCount() {
        return habitCount + quizCount;
    }
}

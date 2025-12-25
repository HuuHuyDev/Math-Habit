package com.kidsapp.data.model;

import java.util.List;

/**
 * Model cho bài test tổng hợp
 * Bài test này sẽ bao gồm câu hỏi từ tất cả các nội dung đã được giao
 */
public class ComprehensiveTest {
    private String id;
    private String title;
    private String description;
    private List<String> contentIds; // Danh sách ID các nội dung được test
    private int totalQuestions;
    private int duration; // Thời gian làm bài (phút)
    private int passingScore; // Điểm đạt (%)
    private boolean isAvailable; // Có thể làm bài không
    private String availabilityMessage; // Thông báo nếu chưa thể làm

    public ComprehensiveTest() {
    }

    public ComprehensiveTest(String id, String title, String description, 
                           List<String> contentIds, int totalQuestions, 
                           int duration, int passingScore, 
                           boolean isAvailable, String availabilityMessage) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.contentIds = contentIds;
        this.totalQuestions = totalQuestions;
        this.duration = duration;
        this.passingScore = passingScore;
        this.isAvailable = isAvailable;
        this.availabilityMessage = availabilityMessage;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getContentIds() {
        return contentIds;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getDuration() {
        return duration;
    }

    public int getPassingScore() {
        return passingScore;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getAvailabilityMessage() {
        return availabilityMessage;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContentIds(List<String> contentIds) {
        this.contentIds = contentIds;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setPassingScore(int passingScore) {
        this.passingScore = passingScore;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setAvailabilityMessage(String availabilityMessage) {
        this.availabilityMessage = availabilityMessage;
    }
}

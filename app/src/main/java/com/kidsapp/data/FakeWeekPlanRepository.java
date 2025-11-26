package com.kidsapp.data;

import com.kidsapp.ui.parent.task_plan.model.WeekDay;
import com.kidsapp.ui.parent.task_plan.model.WeekTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository giả lập dữ liệu kế hoạch tuần
 */
public class FakeWeekPlanRepository {

    /**
     * Tạo danh sách 7 ngày trong tuần
     */
    public static List<WeekDay> getWeekDays() {
        List<WeekDay> weekDays = new ArrayList<>();
        String[] dayLabels = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};

        for (int i = 0; i < 7; i++) {
            WeekDay day = new WeekDay(i, dayLabels[i]);
            weekDays.add(day);
        }

        return weekDays;
    }

    /**
     * Tạo danh sách nhiệm vụ demo cho tuần theo childId
     */
    public static List<WeekTask> getDemoTasks(String childId) {
        if (childId == null) {
            return getDemoTasksForChild1();
        }
        
        switch (childId) {
            case "1":
                return getDemoTasksForChild1();
            case "2":
                return getDemoTasksForChild2();
            case "3":
                return getDemoTasksForChild3();
            default:
                return getDemoTasksForChild1();
        }
    }

    /**
     * Nhiệm vụ cho bé 1 (Hồ Hữu Huy - Lớp 3)
     */
    private static List<WeekTask> getDemoTasksForChild1() {
        List<WeekTask> tasks = new ArrayList<>();

        // Thứ 2
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Đánh răng sáng", "Thói quen tốt mỗi ngày", "habit", 10, 5, 0));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Bài tập Toán", "Phép cộng trong phạm vi 100", "quiz", 20, 10, 0));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Dọn phòng", "Sắp xếp đồ chơi gọn gàng", "habit", 15, 8, 0));

        // Thứ 3
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Đọc sách", "Đọc 10 trang truyện", "habit", 10, 5, 1));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Bài tập Tiếng Việt", "Luyện viết chữ đẹp", "quiz", 20, 10, 1));

        // Thứ 4
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Tập thể dục", "Chạy bộ 15 phút", "habit", 15, 8, 2));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Bài tập Toán", "Phép trừ trong phạm vi 100", "quiz", 20, 10, 2));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Giúp mẹ nấu ăn", "Rửa rau củ", "habit", 10, 5, 2));

        // Thứ 5
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Đánh răng tối", "Thói quen tốt mỗi ngày", "habit", 10, 5, 3));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Bài tập Tiếng Anh", "Học 10 từ vựng mới", "quiz", 20, 10, 3));

        // Thứ 6
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Tưới cây", "Chăm sóc cây trong vườn", "habit", 10, 5, 4));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Bài tập Khoa học", "Thí nghiệm đơn giản", "quiz", 25, 15, 4));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Dọn bàn ăn", "Sau bữa tối", "habit", 10, 5, 4));

        // Thứ 7
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Vẽ tranh", "Sáng tạo tự do", "habit", 15, 8, 5));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Chơi game học tập", "Trò chơi Toán học", "quiz", 20, 10, 5));

        // Chủ nhật
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Đi dạo công viên", "Vận động ngoài trời", "habit", 15, 8, 6));

        // Set level cho các quiz
        for (WeekTask task : tasks) {
            if (task.isQuiz()) {
                task.setLevel((int) (Math.random() * 5) + 1);
            }
        }

        return tasks;
    }

    /**
     * Nhiệm vụ cho bé 2 (Linh - Lớp 2)
     */
    private static List<WeekTask> getDemoTasksForChild2() {
        List<WeekTask> tasks = new ArrayList<>();

        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Học bảng chữ cái", "Ôn lại bảng chữ cái tiếng Việt", "habit", 8, 4, 0));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Bài tập Toán", "Đếm số từ 1-50", "quiz", 15, 8, 0));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Vẽ tranh", "Vẽ gia đình", "habit", 10, 5, 1));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Học từ vựng", "5 từ tiếng Anh mới", "quiz", 15, 8, 1));

        for (WeekTask task : tasks) {
            if (task.isQuiz()) {
                task.setLevel((int) (Math.random() * 3) + 1);
            }
        }

        return tasks;
    }

    /**
     * Nhiệm vụ cho bé 3 (Tuấn - Lớp 4)
     */
    private static List<WeekTask> getDemoTasksForChild3() {
        List<WeekTask> tasks = new ArrayList<>();

        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Học bài", "Ôn tập bài học", "habit", 12, 6, 0));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Bài tập Toán", "Phép nhân chia nâng cao", "quiz", 25, 12, 0));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Viết văn", "Tập làm văn", "quiz", 25, 12, 1));
        tasks.add(new WeekTask(UUID.randomUUID().toString(),
                "Học lập trình", "Scratch cơ bản", "quiz", 30, 15, 4));

        for (WeekTask task : tasks) {
            if (task.isQuiz()) {
                task.setLevel((int) (Math.random() * 3) + 3);
            }
        }

        return tasks;
    }

    /**
     * Lọc nhiệm vụ theo ngày
     */
    public static List<WeekTask> getTasksByDay(List<WeekTask> allTasks, int dayIndex) {
        List<WeekTask> dayTasks = new ArrayList<>();
        for (WeekTask task : allTasks) {
            if (task.getDayIndex() == dayIndex) {
                dayTasks.add(task);
            }
        }
        return dayTasks;
    }

    /**
     * Cập nhật thống kê cho các ngày
     */
    public static void updateWeekDaysStats(List<WeekDay> weekDays, List<WeekTask> allTasks) {
        for (WeekDay day : weekDays) {
            List<WeekTask> dayTasks = getTasksByDay(allTasks, day.getDayIndex());
            day.setTotalTasks(dayTasks.size());
            
            int completed = 0;
            for (WeekTask task : dayTasks) {
                if (task.isCompleted()) {
                    completed++;
                }
            }
            day.setCompletedTasks(completed);
        }
    }
}

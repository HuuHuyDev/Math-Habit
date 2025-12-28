package com.kidsapp.data;

import com.kidsapp.ui.parent.task_plan.model.WeekDay;
import com.kidsapp.ui.parent.task_plan.model.WeekTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Helper class cho logic kế hoạch tuần
 * Chứa các utility methods để tính toán ngày, filter tasks
 */
public class WeekPlanHelper {

    /**
     * Tạo danh sách 7 ngày trong tuần hiện tại
     * Với thông tin isPast, isToday và dateStr
     */
    public static List<WeekDay> getWeekDays() {
        List<WeekDay> weekDays = new ArrayList<>();
        String[] dayLabels = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        
        Calendar today = Calendar.getInstance();
        int todayDayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        // Calendar: Sunday=1, Monday=2, ..., Saturday=7
        // dayIndex: 0=Monday, 1=Tuesday, ..., 6=Sunday
        int todayIndex = todayDayOfWeek - 2;
        if (todayIndex < 0) todayIndex = 6; // Sunday
        
        // Tính ngày đầu tuần (Thứ 2)
        Calendar monday = (Calendar) today.clone();
        monday.add(Calendar.DAY_OF_MONTH, -todayIndex);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            Calendar dayCalendar = (Calendar) monday.clone();
            dayCalendar.add(Calendar.DAY_OF_MONTH, i);
            
            String dateStr = sdf.format(dayCalendar.getTime());
            boolean isPast = i < todayIndex;
            boolean isToday = i == todayIndex;
            
            WeekDay day = new WeekDay(i, dayLabels[i], dateStr, isPast, isToday);
            weekDays.add(day);
        }

        return weekDays;
    }

    /**
     * Lấy ngày bắt đầu tuần (Thứ 2) theo format yyyy-MM-dd
     */
    public static String getWeekStartDate() {
        Calendar today = Calendar.getInstance();
        int todayDayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        int todayIndex = todayDayOfWeek - 2;
        if (todayIndex < 0) todayIndex = 6;
        
        today.add(Calendar.DAY_OF_MONTH, -todayIndex);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(today.getTime());
    }

    /**
     * Lấy ngày kết thúc tuần (Chủ nhật) theo format yyyy-MM-dd
     */
    public static String getWeekEndDate() {
        Calendar today = Calendar.getInstance();
        int todayDayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        int todayIndex = todayDayOfWeek - 2;
        if (todayIndex < 0) todayIndex = 6;
        
        today.add(Calendar.DAY_OF_MONTH, 6 - todayIndex);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(today.getTime());
    }

    /**
     * Lấy index của ngày hôm nay (0=T2, 6=CN)
     */
    public static int getTodayIndex() {
        Calendar today = Calendar.getInstance();
        int todayDayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        int todayIndex = todayDayOfWeek - 2;
        if (todayIndex < 0) todayIndex = 6;
        return todayIndex;
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
     * Lọc tasks trong tuần hiện tại (theo dueDate)
     */
    public static List<WeekTask> filterTasksForCurrentWeek(List<WeekTask> allTasks, List<WeekDay> weekDays) {
        List<WeekTask> weekTasks = new ArrayList<>();
        
        for (WeekTask task : allTasks) {
            int dayIndex = task.getDayIndex();
            if (dayIndex >= 0 && dayIndex < weekDays.size()) {
                // Task thuộc tuần này
                weekTasks.add(task);
            }
        }
        
        return weekTasks;
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

    /**
     * Kiểm tra task có quá hạn không
     */
    public static boolean isTaskOverdue(WeekTask task, List<WeekDay> weekDays) {
        if (task.isCompleted()) return false;
        
        int dayIndex = task.getDayIndex();
        if (dayIndex >= 0 && dayIndex < weekDays.size()) {
            return weekDays.get(dayIndex).isPast();
        }
        return false;
    }
    
    /**
     * Tạo demo tasks cho testing
     */
    public static List<WeekTask> getDemoTasks(String childId) {
        List<WeekTask> demoTasks = new ArrayList<>();
        
        // Thêm một vài demo tasks
        demoTasks.add(new WeekTask("demo1", "Làm bài tập toán", "Hoàn thành 10 bài tập", "quiz", 20, 10, 0));
        demoTasks.add(new WeekTask("demo2", "Đọc sách", "Đọc 30 phút", "habit", 15, 8, 1));
        demoTasks.add(new WeekTask("demo3", "Tập thể dục", "Chạy bộ 15 phút", "habit", 10, 5, 2));
        
        return demoTasks;
    }
}

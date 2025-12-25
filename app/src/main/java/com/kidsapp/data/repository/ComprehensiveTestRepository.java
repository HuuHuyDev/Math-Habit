package com.kidsapp.data.repository;

import com.kidsapp.data.model.ComprehensiveTest;
import com.kidsapp.data.model.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Repository quản lý dữ liệu bài test tổng hợp
 * TODO: Kết nối với API backend khi có
 */
public class ComprehensiveTestRepository {
    
    private static ComprehensiveTestRepository instance;
    
    private ComprehensiveTestRepository() {
    }
    
    public static synchronized ComprehensiveTestRepository getInstance() {
        if (instance == null) {
            instance = new ComprehensiveTestRepository();
        }
        return instance;
    }
    
    /**
     * Lấy thông tin bài test tổng hợp cho một task
     * @param taskId ID của task (ví dụ: "Bài 1: luyện phép cộng")
     * @return ComprehensiveTest object
     * 
     * TODO: Thay thế bằng API call
     * API endpoint: GET /api/tasks/{taskId}/comprehensive-test
     */
    public ComprehensiveTest getComprehensiveTest(String taskId) {
        // Dữ liệu mẫu - sẽ được thay thế bằng API call
        List<String> contentIds = Arrays.asList("1", "2", "3"); // Các nội dung đã mở khóa
        
        // Kiểm tra điều kiện: phải hoàn thành ít nhất 2 nội dung
        boolean isAvailable = true; // TODO: Lấy từ backend
        String message = isAvailable ? "" : "Hãy hoàn thành ít nhất 2 nội dung để mở khóa bài test tổng!";
        
        return new ComprehensiveTest(
            "comprehensive_test_1",
            "Bài test tổng hợp",
            "Kiểm tra tổng hợp kiến thức từ tất cả các bài đã học",
            contentIds,
            20, // 20 câu hỏi
            15, // 15 phút
            70, // Đạt 70% trở lên
            isAvailable,
            message
        );
    }
    
    /**
     * Lấy danh sách câu hỏi cho bài test tổng hợp
     * @param testId ID của bài test
     * @return Danh sách câu hỏi
     * 
     * TODO: Thay thế bằng API call
     * API endpoint: GET /api/comprehensive-tests/{testId}/questions
     */
    public List<Question> getComprehensiveTestQuestions(String testId) {
        // Dữ liệu mẫu - sẽ được thay thế bằng API call
        List<Question> questions = new ArrayList<>();
        
        // Câu hỏi từ nội dung 1: Phép cộng 1 chữ số
        questions.add(createQuestion("ct1", "2 + 3 = ?", 
            new String[]{"A", "4", "B", "5", "C", "6", "D", "7"}, 1));
        questions.add(createQuestion("ct2", "5 + 4 = ?", 
            new String[]{"A", "8", "B", "9", "C", "10", "D", "11"}, 1));
        questions.add(createQuestion("ct3", "7 + 2 = ?", 
            new String[]{"A", "8", "B", "9", "C", "10", "D", "11"}, 1));
        questions.add(createQuestion("ct4", "3 + 6 = ?", 
            new String[]{"A", "8", "B", "9", "C", "10", "D", "11"}, 1));
        
        // Câu hỏi từ nội dung 2: Phép cộng 2 chữ số
        questions.add(createQuestion("ct5", "12 + 15 = ?", 
            new String[]{"A", "25", "B", "26", "C", "27", "D", "28"}, 2));
        questions.add(createQuestion("ct6", "23 + 14 = ?", 
            new String[]{"A", "35", "B", "36", "C", "37", "D", "38"}, 2));
        questions.add(createQuestion("ct7", "31 + 22 = ?", 
            new String[]{"A", "51", "B", "52", "C", "53", "D", "54"}, 2));
        questions.add(createQuestion("ct8", "45 + 13 = ?", 
            new String[]{"A", "56", "B", "57", "C", "58", "D", "59"}, 2));
        
        // Câu hỏi từ nội dung 3: Bài toán minh họa
        questions.add(createQuestion("ct9", "Bạn có 5 quả táo, mẹ cho thêm 3 quả. Hỏi bạn có bao nhiêu quả táo?", 
            new String[]{"A", "6", "B", "7", "C", "8", "D", "9"}, 2));
        questions.add(createQuestion("ct10", "Trong lớp có 12 bạn nam và 15 bạn nữ. Hỏi lớp có bao nhiêu học sinh?", 
            new String[]{"A", "25", "B", "26", "C", "27", "D", "28"}, 2));
        questions.add(createQuestion("ct11", "Bé có 8 viên bi xanh và 6 viên bi đỏ. Hỏi bé có tất cả bao nhiêu viên bi?", 
            new String[]{"A", "12", "B", "13", "C", "14", "D", "15"}, 2));
        questions.add(createQuestion("ct12", "Mẹ mua 10 quả cam và 7 quả chanh. Hỏi mẹ mua tất cả bao nhiêu quả?", 
            new String[]{"A", "15", "B", "16", "C", "17", "D", "18"}, 2));
        
        // Câu hỏi tổng hợp nâng cao
        questions.add(createQuestion("ct13", "15 + 8 = ?", 
            new String[]{"A", "21", "B", "22", "C", "23", "D", "24"}, 2));
        questions.add(createQuestion("ct14", "27 + 19 = ?", 
            new String[]{"A", "44", "B", "45", "C", "46", "D", "47"}, 2));
        questions.add(createQuestion("ct15", "Bạn An có 14 cây bút, bạn Bình có 16 cây bút. Hỏi hai bạn có tất cả bao nhiêu cây bút?", 
            new String[]{"A", "28", "B", "29", "C", "30", "D", "31"}, 2));
        questions.add(createQuestion("ct16", "9 + 7 = ?", 
            new String[]{"A", "14", "B", "15", "C", "16", "D", "17"}, 2));
        questions.add(createQuestion("ct17", "33 + 28 = ?", 
            new String[]{"A", "59", "B", "60", "C", "61", "D", "62"}, 2));
        questions.add(createQuestion("ct18", "Trong vườn có 18 con gà và 12 con vịt. Hỏi có tất cả bao nhiêu con?", 
            new String[]{"A", "28", "B", "29", "C", "30", "D", "31"}, 2));
        questions.add(createQuestion("ct19", "6 + 8 = ?", 
            new String[]{"A", "12", "B", "13", "C", "14", "D", "15"}, 2));
        questions.add(createQuestion("ct20", "42 + 35 = ?", 
            new String[]{"A", "75", "B", "76", "C", "77", "D", "78"}, 2));
        
        return questions;
    }
    
    /**
     * Helper method để tạo Question object
     */
    private Question createQuestion(String id, String title, String[] optionsData, int correctIndex) {
        List<com.kidsapp.data.model.AnswerOption> options = new ArrayList<>();
        for (int i = 0; i < optionsData.length; i += 2) {
            options.add(new com.kidsapp.data.model.AnswerOption(optionsData[i], optionsData[i + 1]));
        }
        
        String explanation = "Đáp án đúng là: " + optionsData[correctIndex * 2 + 1];
        return new Question(id, title, options, correctIndex, explanation);
    }
    
    /**
     * Gửi kết quả bài test tổng hợp lên server
     * @param testId ID của bài test
     * @param correctCount Số câu đúng
     * @param totalCount Tổng số câu
     * @param duration Thời gian làm bài (giây)
     * 
     * TODO: Implement API call
     * API endpoint: POST /api/comprehensive-tests/{testId}/submit
     * Body: { correctCount, totalCount, duration, timestamp }
     */
    public void submitTestResult(String testId, int correctCount, int totalCount, long duration) {
        // TODO: Implement API call để lưu kết quả
        // Hiện tại chỉ log
        android.util.Log.d("ComprehensiveTest", 
            String.format("Submit result - Test: %s, Correct: %d/%d, Duration: %d seconds", 
                testId, correctCount, totalCount, duration));
    }
}

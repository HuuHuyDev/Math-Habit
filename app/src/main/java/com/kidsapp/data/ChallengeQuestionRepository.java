package com.kidsapp.data;

import com.kidsapp.data.model.AnswerOption;
import com.kidsapp.data.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Repository chứa các câu hỏi toán logic, đố mẹo cho phần Thách đấu
 * Các câu hỏi được thiết kế phù hợp cho mọi lứa tuổi (6-12 tuổi)
 * để anh chị em, bạn bè có thể chơi cùng nhau
 */
public class ChallengeQuestionRepository {

    // ==================== TOÁN LOGIC ====================
    private static List<Question> getLogicQuestions() {
        List<Question> questions = new ArrayList<>();

        // Logic đơn giản
        questions.add(new Question("logic1",
            "🧮 Nếu 2 + 2 = 4, thì 3 + 3 = ?",
            createOptions("5", "6", "7", "8"), 1,
            "3 + 3 = 6"));

        questions.add(new Question("logic2",
            "🔢 Số nào tiếp theo: 2, 4, 6, 8, ?",
            createOptions("9", "10", "11", "12"), 1,
            "Dãy số chẵn, mỗi số cách nhau 2"));

        questions.add(new Question("logic3",
            "🎯 Số nào tiếp theo: 1, 3, 5, 7, ?",
            createOptions("8", "9", "10", "11"), 1,
            "Dãy số lẻ, mỗi số cách nhau 2"));

        questions.add(new Question("logic4",
            "🧩 5 + ? = 12",
            createOptions("6", "7", "8", "9"), 1,
            "12 - 5 = 7"));

        questions.add(new Question("logic5",
            "🎲 Nếu An có 5 kẹo, Bình cho thêm 3 kẹo. An có bao nhiêu kẹo?",
            createOptions("7", "8", "9", "10"), 1,
            "5 + 3 = 8 kẹo"));

        // Logic trung bình
        questions.add(new Question("logic6",
            "🔄 Số nào tiếp theo: 1, 2, 4, 8, ?",
            createOptions("10", "12", "14", "16"), 3,
            "Mỗi số gấp đôi số trước: 8 × 2 = 16"));

        questions.add(new Question("logic7",
            "⭐ Số nào tiếp theo: 1, 4, 9, 16, ?",
            createOptions("20", "25", "30", "36"), 1,
            "Dãy số bình phương: 1², 2², 3², 4², 5² = 25"));

        questions.add(new Question("logic8",
            "🎯 ? + ? = 10 và ? - ? = 2. Hai số đó là?",
            createOptions("5 và 5", "6 và 4", "7 và 3", "8 và 2"), 1,
            "6 + 4 = 10 và 6 - 4 = 2"));

        questions.add(new Question("logic9",
            "🧮 Nếu ◯ + ◯ = 8, thì ◯ = ?",
            createOptions("2", "3", "4", "5"), 2,
            "◯ + ◯ = 8 → 2◯ = 8 → ◯ = 4"));

        questions.add(new Question("logic10",
            "🔢 Số nào tiếp theo: 3, 6, 9, 12, ?",
            createOptions("14", "15", "16", "18"), 1,
            "Bảng cửu chương 3: mỗi số cách nhau 3"));

        return questions;
    }

    // ==================== CÂU ĐỐ MẸO ====================
    private static List<Question> getTrickQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new Question("trick1",
            "🤔 Có 3 quả táo, bạn lấy đi 2 quả. Bạn có bao nhiêu quả?",
            createOptions("1 quả", "2 quả", "3 quả", "0 quả"), 1,
            "Bạn LẤY ĐI 2 quả, nên bạn CÓ 2 quả!"));

        questions.add(new Question("trick2",
            "🧠 Một người đàn ông có 3 con gái, mỗi con gái có 1 anh trai. Ông có bao nhiêu con?",
            createOptions("3 con", "4 con", "6 con", "7 con"), 1,
            "3 con gái + 1 con trai (chung) = 4 con"));

        questions.add(new Question("trick3",
            "🎭 Số nào lớn hơn: 100 hay 99 + 1?",
            createOptions("100", "99 + 1", "Bằng nhau", "Không so được"), 2,
            "100 = 99 + 1, hai số bằng nhau!"));

        questions.add(new Question("trick4",
            "🌙 Nửa của 2 + 2 bằng bao nhiêu?",
            createOptions("1", "2", "3", "4"), 2,
            "Nửa của (2 + 2) = Nửa của 4 = 2. Hoặc: (Nửa của 2) + 2 = 1 + 2 = 3"));

        questions.add(new Question("trick5",
            "🎪 Có 10 con cá trong bể, 3 con chết đuối. Còn bao nhiêu con?",
            createOptions("7 con", "10 con", "3 con", "0 con"), 1,
            "Cá không thể chết đuối! Vẫn còn 10 con"));

        questions.add(new Question("trick6",
            "🚌 Xe buýt có 10 người. Trạm 1: 3 người xuống, 5 người lên. Trạm 2: 2 người xuống. Hỏi xe dừng mấy trạm?",
            createOptions("1 trạm", "2 trạm", "3 trạm", "10 trạm"), 1,
            "Đề bài nói rõ: Trạm 1 và Trạm 2 = 2 trạm"));

        questions.add(new Question("trick7",
            "🎂 Nếu hôm qua là ngày mai của ngày kia, thì hôm nay là thứ mấy nếu ngày kia là thứ Hai?",
            createOptions("Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu"), 1,
            "Ngày kia = Thứ Hai → Hôm qua = Thứ Ba → Hôm nay = Thứ Tư"));

        questions.add(new Question("trick8",
            "🔢 Viết số 'mười hai nghìn mười hai' bằng chữ số?",
            createOptions("12012", "12.012", "1212", "120012"), 0,
            "Mười hai nghìn = 12000, mười hai = 12 → 12012"));

        questions.add(new Question("trick9",
            "🎯 Bạn chạy đua và vượt qua người thứ 2. Bạn đang ở vị trí thứ mấy?",
            createOptions("Thứ 1", "Thứ 2", "Thứ 3", "Cuối cùng"), 1,
            "Vượt người thứ 2 = Bạn thay thế vị trí của họ = Thứ 2"));

        questions.add(new Question("trick10",
            "🧮 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 × 0 = ?",
            createOptions("0", "10", "11", "1"), 1,
            "Nhân trước cộng sau: 1×0=0, rồi 1+1+...+1+0 = 10"));

        return questions;
    }

    // ==================== ĐỐ VUI HÌNH ẢNH ====================
    private static List<Question> getFunQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new Question("fun1",
            "🍎🍎🍎 + 🍎🍎 = ?",
            createOptions("4 táo", "5 táo", "6 táo", "7 táo"), 1,
            "3 táo + 2 táo = 5 táo"));

        questions.add(new Question("fun2",
            "🐱 + 🐱 + 🐱 = 15. Vậy 🐱 = ?",
            createOptions("3", "4", "5", "6"), 2,
            "3 × 🐱 = 15 → 🐱 = 5"));

        questions.add(new Question("fun3",
            "🌟 × 🌟 = 16. Vậy 🌟 = ?",
            createOptions("2", "3", "4", "8"), 2,
            "🌟 × 🌟 = 16 → 🌟 = 4 (vì 4 × 4 = 16)"));

        questions.add(new Question("fun4",
            "🎈 + 🎈 = 10, 🎈 + 🎁 = 8. Vậy 🎁 = ?",
            createOptions("2", "3", "4", "5"), 1,
            "🎈 = 5, nên 5 + 🎁 = 8 → 🎁 = 3"));

        questions.add(new Question("fun5",
            "🚗 - 🚗 + 🚗 = ?",
            createOptions("0 xe", "1 xe", "2 xe", "3 xe"), 1,
            "🚗 - 🚗 = 0, rồi 0 + 🚗 = 1 xe"));

        questions.add(new Question("fun6",
            "🍕 = 8, 🍕 ÷ 2 = ?",
            createOptions("2", "3", "4", "6"), 2,
            "8 ÷ 2 = 4"));

        questions.add(new Question("fun7",
            "🐶 + 🐱 = 9, 🐶 - 🐱 = 3. Vậy 🐶 = ?",
            createOptions("4", "5", "6", "7"), 2,
            "🐶 = 6, 🐱 = 3 (vì 6+3=9 và 6-3=3)"));

        questions.add(new Question("fun8",
            "🎮 × 2 = 🎮 + 🎮. Đúng hay sai?",
            createOptions("Đúng", "Sai", "Không biết", "Tùy số"), 0,
            "Luôn đúng! a × 2 = a + a"));

        questions.add(new Question("fun9",
            "🌈 + 🌈 + 🌈 + 🌈 = 20. Vậy 🌈 = ?",
            createOptions("4", "5", "6", "10"), 1,
            "4 × 🌈 = 20 → 🌈 = 5"));

        questions.add(new Question("fun10",
            "🎪 = 7, 🎪 + 🎪 - 4 = ?",
            createOptions("8", "9", "10", "11"), 2,
            "7 + 7 - 4 = 14 - 4 = 10"));

        return questions;
    }

    // ==================== TƯ DUY LOGIC ====================
    private static List<Question> getThinkingQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new Question("think1",
            "🧩 Tìm số khác biệt: 2, 4, 6, 9, 10",
            createOptions("2", "6", "9", "10"), 2,
            "9 là số lẻ, các số còn lại đều chẵn"));

        questions.add(new Question("think2",
            "🎯 Tìm số tiếp theo: 1, 1, 2, 3, 5, 8, ?",
            createOptions("10", "11", "12", "13"), 3,
            "Dãy Fibonacci: mỗi số = tổng 2 số trước (5+8=13)"));

        questions.add(new Question("think3",
            "🔍 Số nào chia hết cho cả 2 và 3?",
            createOptions("8", "9", "10", "12"), 3,
            "12 ÷ 2 = 6 ✓, 12 ÷ 3 = 4 ✓"));

        questions.add(new Question("think4",
            "🧠 Nếu A > B và B > C, thì A ? C",
            createOptions("A < C", "A = C", "A > C", "Không biết"), 2,
            "A > B > C nên A > C"));

        questions.add(new Question("think5",
            "🎲 Tổng các mặt đối diện của xúc xắc luôn bằng?",
            createOptions("6", "7", "8", "9"), 1,
            "1+6=7, 2+5=7, 3+4=7"));

        questions.add(new Question("think6",
            "⏰ Kim giờ chỉ số 3, kim phút chỉ số 12. Bây giờ là mấy giờ?",
            createOptions("3:00", "12:15", "3:12", "12:03"), 0,
            "Kim giờ ở 3, kim phút ở 12 = 3 giờ đúng"));

        questions.add(new Question("think7",
            "📐 Hình vuông có mấy góc vuông?",
            createOptions("2", "3", "4", "5"), 2,
            "Hình vuông có 4 góc, tất cả đều vuông"));

        questions.add(new Question("think8",
            "🔢 Số chẵn nhỏ nhất có 2 chữ số là?",
            createOptions("10", "11", "12", "20"), 0,
            "10 là số chẵn nhỏ nhất có 2 chữ số"));

        questions.add(new Question("think9",
            "🎯 100 - 99 + 98 - 97 + ... + 2 - 1 = ?",
            createOptions("0", "1", "50", "100"), 2,
            "Mỗi cặp (100-99), (98-97)... = 1, có 50 cặp = 50"));

        questions.add(new Question("think10",
            "🧮 Số nào nhân với chính nó bằng chính nó?",
            createOptions("0", "1", "Cả 0 và 1", "Không có"), 2,
            "0 × 0 = 0 và 1 × 1 = 1"));

        return questions;
    }

    // ==================== HELPER METHODS ====================
    private static List<AnswerOption> createOptions(String a, String b, String c, String d) {
        List<AnswerOption> options = new ArrayList<>();
        options.add(new AnswerOption("A", a));
        options.add(new AnswerOption("B", b));
        options.add(new AnswerOption("C", c));
        options.add(new AnswerOption("D", d));
        return options;
    }

    /**
     * Lấy danh sách câu hỏi ngẫu nhiên cho thách đấu
     * @param count Số lượng câu hỏi cần lấy
     * @param topic Chủ đề: "all", "logic", "trick", "fun", "thinking"
     * @return Danh sách câu hỏi đã được xáo trộn
     */
    public static List<Question> getRandomQuestions(int count, String topic) {
        List<Question> allQuestions = new ArrayList<>();

        switch (topic.toLowerCase()) {
            case "logic":
                allQuestions.addAll(getLogicQuestions());
                break;
            case "trick":
                allQuestions.addAll(getTrickQuestions());
                break;
            case "fun":
                allQuestions.addAll(getFunQuestions());
                break;
            case "thinking":
                allQuestions.addAll(getThinkingQuestions());
                break;
            default: // "all" - lấy tất cả
                allQuestions.addAll(getLogicQuestions());
                allQuestions.addAll(getTrickQuestions());
                allQuestions.addAll(getFunQuestions());
                allQuestions.addAll(getThinkingQuestions());
                break;
        }

        // Xáo trộn và lấy số lượng cần thiết
        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, Math.min(count, allQuestions.size()));
    }

    /**
     * Lấy 10 câu hỏi ngẫu nhiên từ tất cả chủ đề
     */
    public static List<Question> getDefaultBattleQuestions() {
        return getRandomQuestions(10, "all");
    }
}

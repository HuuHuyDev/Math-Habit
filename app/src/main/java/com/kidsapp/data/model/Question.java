package com.kidsapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Question implements Parcelable {
    private final String id;
    private final String title;
    private final List<AnswerOption> options;
    private final int correctIndex;
    private final String explanation;
    
    // Lưu đáp án người dùng đã chọn (-1 = chưa chọn)
    private int selectedIndex = -1;

    public Question(String id,
                    String title,
                    List<AnswerOption> options,
                    int correctIndex,
                    String explanation) {
        this.id = id;
        this.title = title;
        this.options = options;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<AnswerOption> getOptions() {
        return options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public String getExplanation() {
        return explanation;
    }
    
    // Lưu đáp án người dùng chọn
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
    
    public int getSelectedIndex() {
        return selectedIndex;
    }
    
    // Kiểm tra câu này đã được trả lời chưa
    public boolean isAnswered() {
        return selectedIndex != -1;
    }
    
    // Kiểm tra câu này trả lời đúng hay sai
    public boolean isCorrect() {
        return selectedIndex == correctIndex;
    }

    // Parcelable implementation
    protected Question(Parcel in) {
        id = in.readString();
        title = in.readString();
        options = new ArrayList<>();
        in.readList(options, AnswerOption.class.getClassLoader());
        correctIndex = in.readInt();
        explanation = in.readString();
        selectedIndex = in.readInt();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeList(options);
        dest.writeInt(correctIndex);
        dest.writeString(explanation);
        dest.writeInt(selectedIndex);
    }
}


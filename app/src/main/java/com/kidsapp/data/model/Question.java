package com.kidsapp.data.model;

import java.util.List;

public class Question {
    private final String id;
    private final String title;
    private final List<AnswerOption> options;
    private final int correctIndex;
    private final String explanation;

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
}


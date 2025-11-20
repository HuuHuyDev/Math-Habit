package com.kidsapp.data.model;

public class AnswerOption {
    private final String label;
    private final String content;

    public AnswerOption(String label, String content) {
        this.label = label;
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public String getContent() {
        return content;
    }
}


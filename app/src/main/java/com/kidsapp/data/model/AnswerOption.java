package com.kidsapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AnswerOption implements Parcelable {
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

    // Parcelable implementation
    protected AnswerOption(Parcel in) {
        label = in.readString();
        content = in.readString();
    }

    public static final Creator<AnswerOption> CREATOR = new Creator<AnswerOption>() {
        @Override
        public AnswerOption createFromParcel(Parcel in) {
            return new AnswerOption(in);
        }

        @Override
        public AnswerOption[] newArray(int size) {
            return new AnswerOption[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeString(content);
    }
}


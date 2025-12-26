package com.kidsapp.data.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request để tạo Child mới
 */
public class CreateChildRequest {
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("nickname")
    private String nickname;
    
    @SerializedName("birthDate")
    private String birthDate;
    
    @SerializedName("grade")
    private Integer grade;
    
    @SerializedName("school")
    private String school;
    
    @SerializedName("avatarUrl")
    private String avatarUrl;
    
    @SerializedName("gender")
    private Boolean gender; // true = Nam, false = Nữ
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("password")
    private String password;

    public CreateChildRequest(String name, String nickname, String birthDate,
                              Integer grade, String school, String avatarUrl,
                              Boolean gender, String username, String password) {
        this.name = name;
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.grade = grade;
        this.school = school;
        this.avatarUrl = avatarUrl;
        this.gender = gender;
        this.username = username;
        this.password = password;
    }

    // Getters
    public String getName() { return name; }
    public String getNickname() { return nickname; }
    public String getBirthDate() { return birthDate; }
    public Integer getGrade() { return grade; }
    public String getSchool() { return school; }
    public String getAvatarUrl() { return avatarUrl; }
    public Boolean getGender() { return gender; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

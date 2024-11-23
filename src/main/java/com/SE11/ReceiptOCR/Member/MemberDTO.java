package com.SE11.ReceiptOCR.Member;

public class MemberDTO {
    private String userId; // user_id와 매핑
    private String name;
    private String email;
    private String password;

    // 기본 생성자
    public MemberDTO() {
    }

    // Getter와 Setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
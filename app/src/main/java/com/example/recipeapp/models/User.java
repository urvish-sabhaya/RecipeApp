package com.example.recipeapp.models;

public class User {

    String user_security, user_name, user_image, user_email, security_question_answer, security_question;

    public User() {
    }

    public String getUser_security() {
        return user_security;
    }

    public void setUser_security(String user_security) {
        this.user_security = user_security;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getSecurity_question_answer() {
        return security_question_answer;
    }

    public void setSecurity_question_answer(String security_question_answer) {
        this.security_question_answer = security_question_answer;
    }

    public String getSecurity_question() {
        return security_question;
    }

    public void setSecurity_question(String security_question) {
        this.security_question = security_question;
    }
}

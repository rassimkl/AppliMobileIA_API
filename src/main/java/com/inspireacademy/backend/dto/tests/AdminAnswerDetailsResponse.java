package com.inspireacademy.backend.dto.tests;

public class AdminAnswerDetailsResponse {

    private Long questionId;
    private String questionContent;
    private String questionType;
    private Integer questionPoints;

    private String studentAnswer;
    private String correctAnswer;

    private Integer awardedPoints;

    // ===== GETTERS =====

    public Long getQuestionId() {
        return questionId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public String getQuestionType() {
        return questionType;
    }

    public Integer getQuestionPoints() {
        return questionPoints;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public Integer getAwardedPoints() {
        return awardedPoints;
    }

    // ===== SETTERS =====

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public void setQuestionPoints(Integer questionPoints) {
        this.questionPoints = questionPoints;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setAwardedPoints(Integer awardedPoints) {
        this.awardedPoints = awardedPoints;
    }
}
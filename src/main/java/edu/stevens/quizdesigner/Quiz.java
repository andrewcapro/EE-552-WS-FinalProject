package edu.stevens.quizdesigner;

import java.util.List;

public class Quiz {
    private String name;
    private List<Question> questions;
    private double score;

    public Quiz() {
        // Data container corresponds to JSON
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public double getScore() {
        return score;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
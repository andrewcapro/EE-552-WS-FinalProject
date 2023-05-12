package edu.stevens.quizdesigner;

import java.util.List;

public class Quiz {
    private String name;
    private List<Question> quiz;
    private double score;

    public Quiz() {
        // Data container corresponds to JSON
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<Question> getQuiz() {
        return quiz;
    }

    public double getScore() {
        return score;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setQuiz(List<Question> quiz) {
        this.quiz = quiz;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
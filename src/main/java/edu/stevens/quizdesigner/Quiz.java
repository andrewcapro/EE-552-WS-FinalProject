package edu.stevens.quizdesigner;

import java.util.List;

public class Quiz {
    // Linter gives warnings about name and score
    // However, they are required to be like this for proper autoResult json export
    private String name;
    private List<Question> questions;
    private double score;

    public Quiz() {
        // Data container corresponds to JSON
    }

    // Getters
    public List<Question> getQuestions() {
        return questions;
    }

    public double getScore() {
        return score;
    }

    // Setters
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
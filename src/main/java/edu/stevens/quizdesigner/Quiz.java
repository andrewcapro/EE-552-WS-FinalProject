package edu.stevens.quizdesigner;

import java.util.List;

public class Quiz {
    // Linter gives issues with name and score
    // They are required to stay the way they are for proper autoResult json export
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
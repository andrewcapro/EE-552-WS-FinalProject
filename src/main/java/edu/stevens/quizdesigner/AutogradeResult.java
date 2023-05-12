package edu.stevens.quizdesigner;

import java.util.List;

public class AutogradeResult {
    private String name;
    private double averageScore;
    private double maxScore;
    private double minScore;
    private List<Quiz> quizs;

    public AutogradeResult() {
        // Data container corresponds to JSON
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public double getMinScore() {
        return minScore;
    }

    public List<Quiz> getQuizs() {
        return quizs;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public void setMinScore(double minScore) {
        this.minScore = minScore;
    }

    public void setQuizs(List<Quiz> quizs) {
        this.quizs = quizs;
    }
}
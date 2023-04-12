package edu.stevens.quizdesigner;

public class Question {
    private String prompt;
    private double points;

    public Question(String prompt, double points) {
        this.prompt = prompt;
        this.points = points;
    }

    public String getPrompt() {
        return prompt;
    }

    public double getPoints() {
        return points;
    }
}
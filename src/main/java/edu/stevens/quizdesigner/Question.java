package edu.stevens.quizdesigner;

import java.util.List;

public class Question {
    private final String prompt;
    private double points;
    private String answer;
    private final String type;
    protected List<String> choices;

    public Question(String prompt, double points, String answer, String type) {
        this.prompt = prompt;
        this.points = points;
        this.answer = answer;
        this.type = type;
    }

    // Getters
    public String getPrompt() {
        return prompt;
    }

    public double getPoints() {
        return points;
    }

    public String getAnswer() {
        return answer;
    }

    public String getType() {
        return type;
    }

    // Setters
    public void setPoints(double points) {
        this.points = points;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
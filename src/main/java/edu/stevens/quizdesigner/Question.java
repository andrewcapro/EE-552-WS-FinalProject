package edu.stevens.quizdesigner;

public class Question {
    private String prompt;
    private double points;
    private String answer;
    private final String type;

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
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
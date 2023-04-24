package edu.stevens.quizdesigner;

public class Question {
    public String prompt;
    public double points;
    public String answer;
    public String type;

    public Question(String prompt, double points, String answer, String type) {
        this.prompt = prompt;
        this.points = points;
        this.answer = answer;
        this.type = type;
    }

}
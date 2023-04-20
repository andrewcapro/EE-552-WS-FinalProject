package edu.stevens.quizdesigner;

public class Question {
    public String prompt;
    public double points;
    public String answer;

    public Question(String prompt, double points, String answer) {
        this.prompt = prompt;
        this.points = points;
        this.answer = answer;
    }

}
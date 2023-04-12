package edu.stevens.quizdesigner;

import java.util.ArrayList;

public class MultipleChoiceQuestion extends Question {
    private ArrayList<String> choices;
    private int answer;

    public MultipleChoiceQuestion(String prompt, double points, ArrayList<String> choices, int answer) {
        super(prompt, points);
        this.choices = choices;
        this.answer = answer;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public String getAnswer() {
        return choices.get(answer);
    }

    @Override
    public String toString() {
        return String.format(
            "Multiple Choice\nQuestion: %s\nPoints: %.1f\nChoices: %s\nAnswer: %s\n", 
            getPrompt(), getPoints(), getChoices(), getAnswer()
        );
    }
}
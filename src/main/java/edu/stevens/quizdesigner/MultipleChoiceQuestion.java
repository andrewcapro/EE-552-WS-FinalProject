package edu.stevens.quizdesigner;

import java.util.List;

public class MultipleChoiceQuestion extends Question {
    private final List<String> choices;

    public MultipleChoiceQuestion(String prompt, double points, String answer, List<String> choices) {
        super(prompt, points, answer, "MC");
        this.choices = choices;
    }

    public List<String> getChoices() {
        return choices;
    }

    @Override
    public String toString() {
        return String.format(
                "Multiple Choice (%.1f points)%n%s",
                getPoints(), getPrompt()
        );
    }
}
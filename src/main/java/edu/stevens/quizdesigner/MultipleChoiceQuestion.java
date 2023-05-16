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
        String promptHalf = String.format(
                "Multiple Choice (%.1f points)%n%s%n",
                getPoints(), getPrompt()
        );

        StringBuilder sb = new StringBuilder();
        sb.append(promptHalf);

        for (int i = 0; i < choices.size(); i++) {
            sb.append(i+1)
                    .append(". ")
                    .append(choices.get(i))
                    .append("\n");
        }

        return sb.toString();
    }
}
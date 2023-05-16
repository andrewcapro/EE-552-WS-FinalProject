package edu.stevens.quizdesigner;

public class FillInTheBlankQuestion extends Question {
    public FillInTheBlankQuestion(String prompt, double points, String answer) {
        super(prompt, points, answer, "FB");
    }

    @Override
    public String toString() {
        return String.format(
                "Fill in the Blank/Free Response (%.1f points)%n%s",
                getPoints(), getPrompt()
        );
    }
}
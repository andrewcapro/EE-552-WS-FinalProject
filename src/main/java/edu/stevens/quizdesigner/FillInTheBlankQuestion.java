package edu.stevens.quizdesigner;

public class FillInTheBlankQuestion extends Question {
    public FillInTheBlankQuestion(String prompt, double points, String answer) {
        super(prompt, points, answer, "FB");
    }

    @Override
    public String toString() {
        return String.format(
                "Fill in the Blank%nQuestion: %s%nPoints: %.1f%nAnswer: %s%n",
                getPrompt(), getPoints(), getAnswer()
        );
    }
}
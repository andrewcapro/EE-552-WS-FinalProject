package edu.stevens.quizdesigner;

public class FillInTheBlankQuestion extends Question {
    public FillInTheBlankQuestion(String prompt, double points) {
        super(prompt, points);
    }

    @Override
    public String toString() {
        return String.format(
            "Fill in the Blank\nQuestion: %s\nPoints: %.1f\n", 
            getPrompt(), getPoints()
        );
    }
}
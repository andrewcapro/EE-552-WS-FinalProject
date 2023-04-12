package edu.stevens.quizdesigner;

public class TrueFalseQuestion extends Question {
    private boolean answer;

    public TrueFalseQuestion(String prompt, double points, boolean answer) {
        super(prompt, points);
        this.answer = answer;
    }

    public boolean getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        return String.format(
            "True/False\nQuestion: %s\nPoints: %.1f\nAnswer: %b\n", 
            getPrompt(), getPoints(), getAnswer()
        );
    }
}
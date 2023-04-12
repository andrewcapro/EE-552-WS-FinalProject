package edu.stevens.quizdesigner;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create some questions
        Question tfq = new TrueFalseQuestion(
            "The sky is blue", 
            1, 
            true
        );
        Question fibq = new FillInTheBlankQuestion(
            "The capital of New Jersey is ___", 
            3
            );
        Question mcq = new MultipleChoiceQuestion(
            "What is the capital of New York?", 
            2, 
            new ArrayList<>(List.of("Albany", "New York City", "Buffalo")),
            0
        );

        // Print out the questions
        System.out.println(mcq);
        System.out.println(tfq);
        System.out.println(fibq);
    }
}
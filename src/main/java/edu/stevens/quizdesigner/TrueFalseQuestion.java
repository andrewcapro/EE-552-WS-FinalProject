 package edu.stevens.quizdesigner;

 public class TrueFalseQuestion extends Question {
     private boolean answer;

     public TrueFalseQuestion(String prompt, double points, String answer ) {
         super(prompt, points, answer,  "TF");
     }

     @Override
     public String toString() {
         return String.format(
             "True/False%nQuestion: %s%nPoints: %.1f%nAnswer: %b%n",
             prompt, points, answer
         );
     }
 }
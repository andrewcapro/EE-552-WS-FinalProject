 package edu.stevens.quizdesigner;

 public class TrueFalseQuestion extends Question {
     public TrueFalseQuestion(String prompt, double points, String answer) {
         super(prompt, points, answer,  "TF");
     }

     @Override
     public String toString() {
         return String.format(
             "True/False (%.1f points)%n%s",
             getPoints(), getPrompt()
         );
     }
 }
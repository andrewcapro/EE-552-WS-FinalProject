 package edu.stevens.quizdesigner;

 public class MultipleChoiceQuestion extends Question {
     private String[] choices;
     private int answer;

     public MultipleChoiceQuestion(String prompt, double points, String answer, String[] choices) {
         super(prompt, points, answer);
         this.choices = choices;
     }

     public String[] getChoices() {
         return choices;
     }

     @Override
     public String toString() {
         return String.format(
             "Multiple Choice%nQuestion: %s%nPoints: %.1f%nChoices: %s%nAnswer: %s%n",
             prompt, points, answer, getChoices()
         );
     }
 }
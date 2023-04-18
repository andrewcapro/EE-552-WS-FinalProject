package edu.stevens.quizdesigner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        ArrayList<Question> quizQuestions = new ArrayList<>();
        
        System.out.print("Enter quiz name: ");
        String quizName = scanner.nextLine();
        
        while (true) {
            System.out.print("Enter question prompt (or 'done' to finish): ");
            String prompt = scanner.nextLine();
            
            if (prompt.equals("done")) {
                break;
            }
            
            System.out.print("Enter question point value: ");
            double points = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Enter question answer: ");
            String answer = scanner.nextLine();
            
            while (true) {
                System.out.print("Enter question type (TF for true/false, MC for multiple choice, or FB for fill-in-the-blank): ");
                String type = scanner.nextLine();
                
                if (type.equalsIgnoreCase("TF")) {
                    TrueFalseQuestion question = new TrueFalseQuestion(prompt, points, answer);
                    quizQuestions.add(question);
                    break;
                } else if (type.equalsIgnoreCase("MC")) {
                    System.out.print("Enter comma-separated answer choices: ");
                    String choicesString = scanner.nextLine();
                    String[] choices = choicesString.split(",");
                    MultipleChoiceQuestion question = new MultipleChoiceQuestion(prompt, points, answer, choices);
                    quizQuestions.add(question);
                    break;
                } else if (type.equalsIgnoreCase("FB")) {
                    FillInTheBlankQuestion question = new FillInTheBlankQuestion(prompt, points, answer);
                    quizQuestions.add(question);
                    break;
                } else {
                    System.out.println("Invalid question type. Please try again.");
                }
            }
        }
        
        // Create JSON object for quiz
        JSONObject quizJSON = new JSONObject();
        quizJSON.put("name", quizName);
        
        JSONArray quizArray = new JSONArray();
        for (Question question : quizQuestions) {
            JSONObject questionJSON = new JSONObject();
            questionJSON.put("prompt", question.getPrompt());
            questionJSON.put("answer", question.getAnswer());
            questionJSON.put("points", question.getPoints());
            
            if (question instanceof TrueFalseQuestion) {
                questionJSON.put("type", "TF");
            } else if (question instanceof MultipleChoiceQuestion) {
                questionJSON.put("type", "MC");
                JSONArray choicesArray = new JSONArray();
                String[] choices = ((MultipleChoiceQuestion) question).getChoices();
                for (String choice : choices) {
                    choicesArray.put(choice);
                }
                questionJSON.put("choices", choicesArray);
            } else if (question instanceof FillInTheBlankQuestion) {
                questionJSON.put("type", "FB");
            }
            
            quizArray.put(questionJSON);
        }
        
        quizJSON.put("quiz", quizArray);
        
        // Export quiz to JSON file
        try {
            FileWriter fileWriter = new FileWriter(quizName + ".json");
            fileWriter.write(quizJSON.toString());
            fileWriter.close();
            System.out.println("Quiz exported successfully to " + quizName + ".json");
        } catch (IOException e) {
            System.out.println("Failed to export quiz: " + e.getMessage());
        }

        AutoGrader.autograde("quizToBeGraded", "exampleAnswerKey.json");
        // Print out the questions
        // System.out.println(mcq);
        // System.out.println(tfq);
        // System.out.println(fibq);
    }
}
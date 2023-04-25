package edu.stevens.quizdesigner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        ArrayList<Question> quizQuestions = new ArrayList<>();
        System.out.println("Welcome to the Quiz Designer!");
        while (true){
            //intro selection
            System.out.println("1) Create a quiz");
            System.out.println("2) Edit a quiz");
            System.out.println("3) Take a quiz");
            System.out.println("4) AutoGrade quizzes");
            System.out.println("5) Exit");
            
            System.out.print("Please select an option(1, 2, 3, 4, or 5): ");


            String option = scanner.nextLine();

            //option 1: creating a quiz and exporting to createdQuiz folder
            if (option.equals("1")){
                System.out.println("\nYou have selected to create a quiz");
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
                    System.out.println();
                }
                
                // Create JSON object for quiz
                JSONObject quizJSON = new JSONObject();
                quizJSON.put("name", quizName);
                
                JSONArray quizArray = new JSONArray();
                for (Question question : quizQuestions) {
                    JSONObject questionJSON = new JSONObject();
                    questionJSON.put("prompt", question.prompt);
                    questionJSON.put("answer", question.answer);
                    questionJSON.put("points", question.points);
                    
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
                    FileWriter fileWriter = new FileWriter("createdQuiz/"+ quizName + ".json");
                    fileWriter.write(quizJSON.toString());
                    fileWriter.close();
                    System.out.println("Quiz exported successfully to createdQuiz/"+ quizName + ".json");
                } catch (IOException e) {
                    System.out.println("Failed to export quiz: " + e.getMessage());
                }
                System.out.println("_______________________________________________________");
            }
            else if (option.equals("2")){
                System.out.println("\nYou have selected to edit a quiz");
                System.out.println("Quizzes available to edit are from the quizToEdit folder");
                System.out.println("Please make sure the quiz you want to edit is in the quizToEdit folder");
                System.out.println("Please press enter to continue");
                scanner.nextLine();
                System.out.println("_______________________________________________________");



                try{
                    File file = new File("quizToEdit");
                    File[] files = file.listFiles();
                    System.out.println("Files in quizTOEdit folder:");
                    for (int i = 0; i < files.length; i++){
                        System.out.println((i+1) + ") " + files[i].getName());
                    }

                    System.out.print("Please select a quiz to edit: ");
                    int quizToEdit = Integer.parseInt(scanner.nextLine());
                    String quizName = files[quizToEdit-1].getName();
                    System.out.println("You have selected to edit " + quizName);
                    System.out.println("_______________________________________________________");
                    System.out.println("Quiz Questions:");
                    ArrayList<Question> quizQuestions2 = new ArrayList<>();
                    
                    // Read quiz from JSON file
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    try {
                        FileReader fileReader = new FileReader("quizToEdit/" + quizName);
                        Quiz quiz = gson.fromJson(fileReader, Quiz.class);

                        quizQuestions2 = quiz.quiz;
                        
                        while (true){
                            for (int i = 0; i < quizQuestions2.size(); i++) {
                                System.out.println((i+1) + ") " + quizQuestions2.get(i).prompt);
                            }
                            System.out.print("0) Add a question\n-1) Finish editing\n");
                            System.out.print("\nPlease select a question/option: ");
                            int questionToEdit = Integer.parseInt(scanner.nextLine());
                            if (questionToEdit == -1){
                                break;
                            } else if (questionToEdit == 0){
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
                                            quizQuestions2.add(question);
                                            break;
                                        } else if (type.equalsIgnoreCase("MC")) {
                                            System.out.print("Enter comma-separated answer choices: ");
                                            String choicesString = scanner.nextLine();
                                            String[] choices = choicesString.split(",");
                                            MultipleChoiceQuestion question = new MultipleChoiceQuestion(prompt, points, answer, choices);
                                            quizQuestions2.add(question);
                                            break;
                                        } else if (type.equalsIgnoreCase("FB")) {
                                            FillInTheBlankQuestion question = new FillInTheBlankQuestion(prompt, points, answer);
                                            quizQuestions2.add(question);
                                            break;
                                        } else {
                                            System.out.println("Invalid question type. Please try again.");
                                        }
                                    }
                                    System.out.println();
                                    break;
                                }
                            } else if (questionToEdit > 0 && questionToEdit <= quizQuestions2.size()){


                                
                            Question question = quizQuestions2.get(questionToEdit-1);
                            System.out.println("_____________________________________________________________________________________");
                            System.out.println("You have selected to edit question " + questionToEdit + ": " + question.prompt);
                        
                            System.out.println("\nQuestion point value: " + question.points);
                            System.out.print("Enter new question point value (or 'skip' to keep the same): ");
                            String points = scanner.nextLine();
                            if (!points.equals("skip")) {
                                question.points = Double.parseDouble(points);
                            }

                            System.out.println("Question answer: " + question.answer);
                            System.out.print("Enter new question answer (or 'skip' to keep the same): ");
                            String answer = scanner.nextLine();
                            if (!answer.equals("skip")) {
                                question.answer = answer;
                            }
                            
                            System.out.println("\nWould you like to edit the question type? (Y/N): ");
                            String editType = scanner.nextLine();
                            
                            if (editType.equalsIgnoreCase("Y")){
                                System.out.println("Question type: " + question.type);
                                System.out.print("Enter new question type (TF, MC, or FB or 'skip' to keep the same): ");
                                String type = scanner.nextLine();
                                if (!type.equals("skip") && !type.equals(question.type)) {
                                    if (type.equalsIgnoreCase("TF")) {
                                        System.out.print("Enter new answer (T/F): ");
                                        String newAnswer = scanner.nextLine();
                                        if (newAnswer.equalsIgnoreCase("T") || newAnswer.equalsIgnoreCase("F")) {
                                            System.out.println("Would you like to change the question prompt? (Y/N)");
                                            String changePrompt = scanner.nextLine();
                                            if (changePrompt.equalsIgnoreCase("Y")) {
                                                System.out.print("Enter new question prompt: ");
                                                String newPrompt = scanner.nextLine();
                                                TrueFalseQuestion newQuestion = new TrueFalseQuestion(newPrompt, question.points, newAnswer);
                                                quizQuestions2.set(questionToEdit-1, newQuestion);
                                            } else {
                                                TrueFalseQuestion newQuestion = new TrueFalseQuestion(question.prompt, question.points, newAnswer);
                                                quizQuestions2.set(questionToEdit-1, newQuestion);
                                            }
                                        } else {
                                            System.out.println("Invalid answer. Question type will not be changed.");
                                        }
                                    } else if (type.equalsIgnoreCase("MC")) {
                                        System.out.print("Enter comma-separated answer choices: ");
                                        String choicesString = scanner.nextLine();
                                        String[] choices = choicesString.split(",");
                                        System.out.print("Would you like to change the question prompt? (Y/N)");
                                        String changePrompt = scanner.nextLine();
                                        if (changePrompt.equalsIgnoreCase("Y")) {
                                            System.out.print("Enter new question prompt: ");
                                            String newPrompt = scanner.nextLine();
                                            MultipleChoiceQuestion newQuestion = new MultipleChoiceQuestion(newPrompt, question.points, question.answer, choices);
                                            quizQuestions2.set(questionToEdit-1, newQuestion);
                                        } else {
                                            MultipleChoiceQuestion newQuestion = new MultipleChoiceQuestion(question.prompt, question.points, question.answer, choices);
                                            quizQuestions2.set(questionToEdit-1, newQuestion);
                                        }
                                    } else if (type.equalsIgnoreCase("FB")) {
                                        System.out.print("Would you like to change the question prompt? (Y/N)");
                                        String changePrompt = scanner.nextLine();
                                        if (changePrompt.equalsIgnoreCase("Y")) {
                                            System.out.print("Enter new question prompt: ");
                                            String newPrompt = scanner.nextLine();
                                            FillInTheBlankQuestion newQuestion = new FillInTheBlankQuestion(newPrompt, question.points, question.answer);
                                            quizQuestions2.set(questionToEdit-1, newQuestion);
                                        } else {
                                            FillInTheBlankQuestion newQuestion = new FillInTheBlankQuestion(question.prompt, question.points, question.answer);
                                            quizQuestions2.set(questionToEdit-1, newQuestion);
                                        }
                                    }
                                }
                                }else{
                                    System.out.println("Question type will not be changed.");}
                            }

                        }

                        // Write quiz to JSON file

                        Quiz editedQuiz = new Quiz();
                        editedQuiz.quiz = quizQuestions2;
                        editedQuiz.name = quizName;
                        editedQuiz.score = 0;


                        try {
                            GsonBuilder builder = new GsonBuilder();
                            builder.setPrettyPrinting();
                            Gson gson2 = builder.create();
                            File path = new File("quizEdited/"+ quizName);
                            FileWriter writer = new FileWriter(path);
                            writer.write(gson2.toJson(editedQuiz));
                            writer.close();
                            System.out.println("Quiz successfully edited\n");
                            System.out.println("_______________________________________________________");

                        } catch (Exception e) {
                            System.out.println("Failed to write quiz: " + e.getMessage());
                        }

                    } catch (IOException e) {
                        System.out.println("Failed to read quiz: " + e.getMessage());
                    }

                

                }
                catch (Exception e){
                    System.out.println("\nNo quizzes to edit");
                    System.out.println("_______________________________________________________");
                    continue;
                }
                









            }
            else if (option.equals("3")){
                System.out.println("You have selected to take a quiz");
                break;
            }
            //Option 4: autograding the quizzes in quizToBeGraded folder and exporting results in autoResults folder
            else if (option.equals("4")){
                System.out.println("\nYou have selected to autograde quizzes");
                System.out.println("Please put all quizzes to be graded in the quizToBeGraded folder");
                System.out.println("Please put the answer key in the quizAnswerKey folder");
                System.out.println("AnyFiles in autoResult will be deleted and replaced with new results");
                System.out.println("Please Press Enter when files are in the correct folders");
                scanner.nextLine();
                System.out.println("_______________________________________________________");
                AutoGrader.autograde();
                System.out.println("_______________________________________________________");
            }
            else if (option.equals("5")){
                System.out.println("You have selected to exit");
                break;
            }
            else{
                System.out.println("Invalid option. Please try again.");
            }
        }
        



        




        
        

        
        // Print out the questions
        // System.out.println(mcq);
        // System.out.println(tfq);
        // System.out.println(fibq);
    }
}
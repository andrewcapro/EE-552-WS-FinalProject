package edu.stevens.quizdesigner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class QuizDesigner {
    private static final String QUIZ_FOLDER = "quizzes/";
    private static final String FILE_EXTENSION = ".json";

    private QuizDesigner() {
    }
    private static Question createQuestion(String prompt) {
        double points;
        try {
            System.out.print("Enter question point value: ");
            points = Main.scanner.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Invalid point value, stopping question creation");
            return new Question("", 0, "", "");
        } finally {
            Main.scanner.nextLine();
        }

        while (true) {
            System.out.print("Enter question type (TF for true/false, MC for multiple choice, or FB for fill-in-the-blank): ");
            String type = Main.scanner.nextLine().toUpperCase();
            String answer;

            switch (type) {
                case "TF" -> {
                    do {
                        System.out.print("Enter question answer (must be true/false): ");
                        answer = Main.scanner.nextLine().toLowerCase();
                    } while (!answer.equals("true") && !answer.equals("false"));

                    return new TrueFalseQuestion(prompt, points, answer);
                }
                case "MC" -> {
                    System.out.print("Enter comma-separated answer choices: ");
                    String choicesString = Main.scanner.nextLine();
                    String[] choicesArr = choicesString.split(",");
                    List<String> choices = new ArrayList<>();
                    for (String choice : choicesArr) {
                        choices.add(choice.trim());
                    }

                    do {
                        System.out.print("Enter question answer (must be one of the valid answer choices): ");
                        answer = Main.scanner.nextLine();
                    } while (!choices.contains(answer));

                    return new MultipleChoiceQuestion(prompt, points, answer, choices);
                }
                case "FB" -> {
                    System.out.print("Enter question answer: ");
                    answer = Main.scanner.nextLine().trim();
                    return new FillInTheBlankQuestion(prompt, points, answer);
                }
                default -> System.out.println("Invalid question type. Please try again.");
            }
        }
    }

    public static void createQuiz() {
        List<Question> quizQuestions = new ArrayList<>();

        System.out.println("You have selected to create a quiz");
        System.out.print("Enter quiz name: ");
        String quizName = Main.scanner.nextLine();

        while (true) {
            System.out.print("Enter question prompt (or 'done' to finish): ");
            String prompt = Main.scanner.nextLine();

            if (prompt.equals("done")) {
                break;
            }

            Question question = createQuestion(prompt);
            if (!question.getType().equals("")) quizQuestions.add(question);
            System.out.println();
        }

        // Create JSON object for quiz
        Gson gson = new Gson();
        JsonObject quizJSON = new JsonObject();

        JsonArray questionsArray = new JsonArray();
        for (Question question : quizQuestions) {
            JsonObject questionJSON = new JsonObject();
            questionJSON.addProperty("prompt", question.getPrompt());
            questionJSON.addProperty("answer", question.getAnswer());
            questionJSON.addProperty("points", question.getPoints());

            if (question instanceof TrueFalseQuestion) {
                questionJSON.addProperty("type", "TF");
            } else if (question instanceof MultipleChoiceQuestion mcq) {
                questionJSON.addProperty("type", "MC");
                JsonArray choicesArray = new JsonArray();
                List<String> choices = mcq.getChoices();
                for (String choice : choices) {
                    choicesArray.add(choice);
                }
                questionJSON.add("choices", choicesArray);
            } else if (question instanceof FillInTheBlankQuestion) {
                questionJSON.addProperty("type", "FB");
            }
            questionsArray.add(questionJSON);
        }
        quizJSON.add("questions", questionsArray);

        // Export quiz to JSON file
        try (FileWriter fileWriter = new FileWriter(QUIZ_FOLDER + quizName + FILE_EXTENSION)) {
            gson.toJson(quizJSON, fileWriter);
            System.out.println("Quiz exported successfully to " + QUIZ_FOLDER + quizName + FILE_EXTENSION);
        } catch (IOException e) {
            System.out.println("Failed to export quiz: " + e.getMessage());
        }
    }

    private static void editQuestion(List<Question> quizQuestions, int questionToEdit) {
        Question question = quizQuestions.get(questionToEdit - 1);
        int option;

        do {
            System.out.println("1) Change prompt");
            System.out.println("2) Change points");
            System.out.println("3) Change question type and answer");
            System.out.println("4) Exit");
            System.out.print("Please select an option (1, 2, 3, or 4): ");
            option = Main.scanner.nextInt();
            Main.scanner.nextLine();

            switch (option) {
                case 1 -> {
                    System.out.println("\nCurrent prompt: " + question.getPrompt());
                    System.out.print("Enter new question prompt: ");
                    String newPrompt = Main.scanner.nextLine();
                    question.setPrompt(newPrompt);
                    System.out.println("Prompted successfully changed");
                }
                case 2 -> {
                    System.out.println("\nCurrent point value: " + question.getPoints());
                    System.out.print("Enter new question point value: ");
                    double points = Main.scanner.nextDouble();
                    question.setPoints(points);
                    System.out.println("Points successfully changed");
                }
                case 3 -> {
                    System.out.println("Current question type: " + question.getType());
                    System.out.print("Enter new question type (TF for true/false, MC for multiple choice, or FB for fill-in-the-blank): ");
                    String newType = Main.scanner.nextLine().toUpperCase();
                    String newAnswer;

                    switch (newType) {
                        case "TF" -> {
                            do {
                                System.out.print("Enter new answer (must be true/false): ");
                                newAnswer = Main.scanner.nextLine().toLowerCase();
                            } while (!newAnswer.equals("true") && !newAnswer.equals("false"));

                            Question newQuestion = new TrueFalseQuestion(
                                    question.getPrompt(), question.getPoints(), newAnswer);
                            quizQuestions.set(questionToEdit - 1, newQuestion);
                            System.out.println("Question and answer successfully changed to TF");
                        }

                        case "MC" -> {
                            System.out.print("Enter new comma-separated answer choices: ");
                            String newChoicesStr = Main.scanner.nextLine();
                            String[] newChoicesArr = newChoicesStr.split(",");
                            List<String> newChoices = new ArrayList<>();
                            for (String choice : newChoicesArr) {
                                newChoices.add(choice.trim());
                            }

                            do {
                                System.out.print("Enter new question answer (must be one of the valid answer choices): ");
                                newAnswer = Main.scanner.nextLine();
                            } while (!newChoices.contains(newAnswer));

                            Question newQuestion = new MultipleChoiceQuestion(
                                    question.getPrompt(), question.getPoints(), newAnswer, newChoices);
                            quizQuestions.set(questionToEdit - 1, newQuestion);
                            System.out.println("Question and answer successfully changed to MC");
                        }

                        case "FB" -> {
                            System.out.print("Enter new answer: ");
                            newAnswer = Main.scanner.nextLine().trim();

                            Question newQuestion = new FillInTheBlankQuestion(
                                    question.getPrompt(), question.getPoints(), newAnswer);
                            quizQuestions.set(questionToEdit - 1, newQuestion);
                            System.out.println("Question and answer successfully changed to FB");
                        }

                        default -> System.out.println("Invalid type, question type and answer will not be edited");
                    }
                }
                case 4 -> System.out.println("You have selected to exit");
                default -> System.out.println("Invalid option. Please try again.");
            }
        } while (option != 4);
    }

    public static void editQuiz() {
        System.out.println("You have selected to edit a quiz");
        System.out.println("Please make sure the quiz you want to edit is in the quizzes folder");
        System.out.println("Please press ENTER to continue");
        Main.scanner.nextLine();
        File file = new File(QUIZ_FOLDER);
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No quizzes found in the 'quizzes' folder.");
            return;
        }

        System.out.println("Files in quizzes folder:");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ") " + files[i].getName());
        }

        System.out.print("Please select a quiz to edit: ");
        int quizToEdit = Main.scanner.nextInt();
        String quizName = files[quizToEdit - 1].getName();
        quizName = quizName.substring(0, quizName.lastIndexOf("."));
        System.out.println("You have selected to edit " + quizName + FILE_EXTENSION + "\n");
        List<Question> quizQuestions;

        // Read quiz from JSON file
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileReader fileReader = new FileReader(QUIZ_FOLDER + quizName + FILE_EXTENSION);
            Quiz quiz = gson.fromJson(fileReader, Quiz.class);
            quizQuestions = quiz.getQuestions();
            while (true) {
                System.out.println("Quiz questions:");
                for (int i = 0; i < quizQuestions.size(); i++) {
                    System.out.println((i + 1) + ") " + quizQuestions.get(i).getPrompt());
                }
                System.out.print("0) Add a question\n-1) Finish editing\n");
                System.out.print("\nPlease select a question/option: ");
                int questionToEdit = Main.scanner.nextInt();
                Main.scanner.nextLine();

                if (questionToEdit == -1) {
                    break;
                } else if (questionToEdit == 0) {
                    System.out.print("Enter question prompt: ");
                    String prompt = Main.scanner.nextLine();
                    quizQuestions.add(createQuestion(prompt));
                    System.out.println();
                } else if (questionToEdit > 0 && questionToEdit <= quizQuestions.size()) {
                    editQuestion(quizQuestions, questionToEdit);
                }

                // Write quiz to JSON file
                Quiz editedQuiz = new Quiz();
                editedQuiz.setQuestions(quizQuestions);

                File path = new File(QUIZ_FOLDER + quizName + FILE_EXTENSION);
                try (FileWriter writer = new FileWriter(path)) {
                    writer.write(gson.toJson(editedQuiz));
                    System.out.println("Quiz successfully edited\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read quiz: " + e.getMessage());
        }
    }
}

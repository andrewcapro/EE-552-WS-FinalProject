package edu.stevens.quizdesigner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main {
    private static final String QUIZ_FOLDER = "quizzes/";
    private static final String FILE_EXTENSION = ".json";
    private static final Scanner scanner = new Scanner(System.in);

    public static void create() {
        List<Question> quizQuestions = new ArrayList<>();

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
            double points = scanner.nextDouble();

            while (true) {
                System.out.print("Enter question type (TF for true/false, MC for multiple choice, or FB for fill-in-the-blank): ");
                String type = scanner.nextLine().toUpperCase();

                if (type.equals("TF")) {
                    System.out.print("Enter question answer: ");
                    String answer = scanner.nextLine();

                    Question question = new TrueFalseQuestion(prompt, points, answer);
                    quizQuestions.add(question);
                    break;
                } else if (type.equals("MC")) {
                    System.out.print("Enter comma-separated answer choices: ");
                    String choicesString = scanner.nextLine();
                    String[] choices = choicesString.split(",");

                    System.out.print("Enter question answer: ");
                    String answer = scanner.nextLine();

                    Question question = new MultipleChoiceQuestion(prompt, points, answer, choices);
                    quizQuestions.add(question);
                    break;
                } else if (type.equals("FB")) {
                    System.out.print("Enter question answer: ");
                    String answer = scanner.nextLine();

                    Question question = new FillInTheBlankQuestion(prompt, points, answer);
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
            questionJSON.put("prompt", question.getPrompt());
            questionJSON.put("answer", question.getAnswer());
            questionJSON.put("points", question.getPoints());

            if (question instanceof TrueFalseQuestion) {
                questionJSON.put("type", "TF");
            } else if (question instanceof MultipleChoiceQuestion mcq) {
                questionJSON.put("type", "MC");
                JSONArray choicesArray = new JSONArray();
                String[] choices = mcq.getChoices();
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
        try (FileWriter fileWriter = new FileWriter(QUIZ_FOLDER + quizName + FILE_EXTENSION)) {
            fileWriter.write(quizJSON.toString());
            System.out.println("Quiz exported successfully to " + QUIZ_FOLDER + quizName + FILE_EXTENSION);
        } catch (IOException e) {
            System.out.println("Failed to export quiz: " + e.getMessage());
        }
        System.out.println("_______________________________________________________");
    }

    public static void edit() {
        // TODO implement
    }

    public static void take() {
        // TODO implement
    }

    public static void main(String[] args) {
        try (scanner) {
            System.out.println("Welcome to the Quiz Designer!");
            int option;

            do {
                //Intro selection
                System.out.println("1) Create a quiz");
                System.out.println("2) Edit a quiz");
                System.out.println("3) Take a quiz");
                System.out.println("4) Autograde quizzes");
                System.out.println("5) Exit");

                System.out.print("Please select an option (1, 2, 3, 4, or 5): ");
                option = scanner.nextInt();
                scanner.nextLine();
                System.out.println("option=" + option);

                //Option 1: Creating a quiz and exporting it to the quizzes folder
                switch (option) {
                    case 1:
                        create();
                        break;
                    case 2:
                        System.out.println("\nYou have selected to edit a quiz");
                        System.out.println("Please make sure the quiz you want to edit is in the quizzes folder");
                        System.out.println("Please press ENTER to continue");
                        scanner.nextLine();
                        System.out.println("_______________________________________________________");

                        try {
                            File file = new File(QUIZ_FOLDER);
                            File[] files = file.listFiles();
                            System.out.println("Files in quizzes folder:");
                            for (int i = 0; i < files.length; i++) {
                                System.out.println((i + 1) + ") " + files[i].getName());
                            }

                            System.out.print("Please select a quiz to edit: ");
                            int quizToEdit = Integer.parseInt(scanner.nextLine());
                            String quizName = files[quizToEdit - 1].getName();
                            System.out.println("You have selected to edit " + quizName);
                            System.out.println("_______________________________________________________");
                            System.out.println("Quiz Questions:");
                            List<Question> quizQuestions2;

                            // Read quiz from JSON file
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            try {
                                FileReader fileReader = new FileReader(QUIZ_FOLDER + quizName);
                                Quiz quiz = gson.fromJson(fileReader, Quiz.class);
                                quizQuestions2 = quiz.getQuiz();

                                while (true) {
                                    for (int i = 0; i < quizQuestions2.size(); i++) {
                                        System.out.println((i + 1) + ") " + quizQuestions2.get(i).getPrompt());
                                    }
                                    System.out.print("0) Add a question\n-1) Finish editing\n");
                                    System.out.print("\nPlease select a question/option: ");
                                    int questionToEdit = Integer.parseInt(scanner.nextLine());
                                    if (questionToEdit == -1) {
                                        break;
                                    } else if (questionToEdit == 0) {
                                            System.out.print("Enter question prompt (or 'done' to finish): ");
                                            String prompt = scanner.nextLine();

                                            if (prompt.equals("done")) {
                                                continue;
                                            }

                                            System.out.print("Enter question point value: ");
                                            double points = Double.parseDouble(scanner.nextLine());

                                            while (true) {
                                                System.out.print("Enter question type (TF for true/false, MC for multiple choice, or FB for fill-in-the-blank): ");
                                                String type = scanner.nextLine().toUpperCase();

                                                if (type.equals("TF")) {
                                                    System.out.print("Enter question answer: ");
                                                    String answer = scanner.nextLine();

                                                    Question question = new TrueFalseQuestion(prompt, points, answer);
                                                    quizQuestions2.add(question);
                                                    break;
                                                } else if (type.equals("MC")) {
                                                    System.out.print("Enter comma-separated answer choices: ");
                                                    String choicesString = scanner.nextLine();
                                                    String[] choices = choicesString.split(",");

                                                    System.out.print("Enter question answer: ");
                                                    String answer = scanner.nextLine();

                                                    Question question = new MultipleChoiceQuestion(prompt, points, answer, choices);
                                                    quizQuestions2.add(question);
                                                    break;
                                                } else if (type.equals("FB")) {
                                                    System.out.print("Enter question answer: ");
                                                    String answer = scanner.nextLine();

                                                    Question question = new FillInTheBlankQuestion(prompt, points, answer);
                                                    quizQuestions2.add(question);
                                                    break;
                                                } else {
                                                    System.out.println("Invalid question type. Please try again.");
                                                }
                                            }
                                            System.out.println();
                                    } else if (questionToEdit > 0 && questionToEdit <= quizQuestions2.size()) {
                                        Question question = quizQuestions2.get(questionToEdit - 1);
                                        System.out.println("_____________________________________________________________________________________");
                                        System.out.println("You have selected to edit question " + questionToEdit + ": " + question.getPrompt());

                                        System.out.println("\nQuestion point value: " + question.getPoints());
                                        System.out.print("Enter new question point value (or 'skip' to keep the same): ");
                                        String points = scanner.nextLine();
                                        if (!points.equals("skip")) {
                                            question.setPoints(Double.parseDouble(points));
                                        }

                                        System.out.println("Question answer: " + question.getAnswer());
                                        System.out.print("Enter new question answer (or 'skip' to keep the same): ");
                                        String answer = scanner.nextLine();
                                        if (!answer.equals("skip")) {
                                            question.setAnswer(answer);
                                        }

                                        System.out.println("\nWould you like to edit the question type? (Y/N): ");
                                        String editType = scanner.nextLine();

                                        if (editType.equalsIgnoreCase("Y")) {
                                            System.out.println("Question type: " + question.getType());
                                            System.out.print("Enter new question type (TF, MC, or FB or 'skip' to keep the same): ");
                                            String type = scanner.nextLine();
                                            if (!type.equals("skip") && !type.equals(question.getType())) {
                                                if (type.equalsIgnoreCase("TF")) {
                                                    System.out.print("Enter new answer (T/F): ");
                                                    String newAnswer = scanner.nextLine();
                                                    if (newAnswer.equalsIgnoreCase("T") || newAnswer.equalsIgnoreCase("F")) {
                                                        System.out.println("Would you like to change the question prompt? (Y/N)");
                                                        String changePrompt = scanner.nextLine();
                                                        if (changePrompt.equalsIgnoreCase("Y")) {
                                                            System.out.print("Enter new question prompt: ");
                                                            String newPrompt = scanner.nextLine();
                                                            TrueFalseQuestion newQuestion = new TrueFalseQuestion(
                                                                    newPrompt, question.getPoints(), newAnswer);
                                                            quizQuestions2.set(questionToEdit - 1, newQuestion);
                                                        } else {
                                                            TrueFalseQuestion newQuestion = new TrueFalseQuestion(
                                                                    question.getPrompt(), question.getPoints(), newAnswer);
                                                            quizQuestions2.set(questionToEdit - 1, newQuestion);
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
                                                        MultipleChoiceQuestion newQuestion = new MultipleChoiceQuestion(
                                                                newPrompt, question.getPoints(), question.getAnswer(), choices);
                                                        quizQuestions2.set(questionToEdit - 1, newQuestion);
                                                    } else {
                                                        MultipleChoiceQuestion newQuestion = new MultipleChoiceQuestion(
                                                                question.getPrompt(), question.getPoints(), question.getAnswer(), choices);
                                                        quizQuestions2.set(questionToEdit - 1, newQuestion);
                                                    }
                                                } else if (type.equalsIgnoreCase("FB")) {
                                                    System.out.print("Would you like to change the question prompt? (Y/N)");
                                                    String changePrompt = scanner.nextLine();
                                                    if (changePrompt.equalsIgnoreCase("Y")) {
                                                        System.out.print("Enter new question prompt: ");
                                                        String newPrompt = scanner.nextLine();
                                                        FillInTheBlankQuestion newQuestion = new FillInTheBlankQuestion(
                                                                newPrompt, question.getPoints(), question.getAnswer());
                                                        quizQuestions2.set(questionToEdit - 1, newQuestion);
                                                    } else {
                                                        FillInTheBlankQuestion newQuestion = new FillInTheBlankQuestion(
                                                                question.getPrompt(), question.getPoints(), question.getAnswer());
                                                        quizQuestions2.set(questionToEdit - 1, newQuestion);
                                                    }
                                                }
                                            }
                                        } else {
                                            System.out.println("Question type will not be changed.");
                                        }
                                    }

                                }

                                // Write quiz to JSON file
                                Quiz editedQuiz = new Quiz();
                                editedQuiz.setQuiz(quizQuestions2);
                                editedQuiz.setName(quizName);
                                editedQuiz.setScore(0);

                                try {
                                    GsonBuilder builder = new GsonBuilder();
                                    builder.setPrettyPrinting();
                                    Gson gson2 = builder.create();
                                    File path = new File(QUIZ_FOLDER + quizName);
                                    path.delete();

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
                        } catch (Exception e) {
                            System.out.println("\nNo quizzes to edit");
                            System.out.println("_______________________________________________________");
                        }
                        break;
                    // Option 3: Taking a quiz
                    case 3:
                        System.out.println("\nYou have selected to take a quiz");

                        File quizFolder = new File(QUIZ_FOLDER);
                        File[] quizFiles = quizFolder.listFiles();
                        if (quizFiles == null || quizFiles.length == 0) {
                            System.out.println("No quizzes found in the 'quizzes' folder.");
                            System.out.println("_______________________________________________________");
                            return;
                        }

                        // Prompt user to select a quiz
                        System.out.println("Select a quiz to take:");
                        for (int i = 0; i < quizFiles.length; i++) {
                            System.out.println((i + 1) + ". " + quizFiles[i].getName().replace(FILE_EXTENSION, ""));
                        }
                        int quizChoice = Integer.parseInt(scanner.nextLine()) - 1;
                        File quizFile = quizFiles[quizChoice];

                        // Load quiz from JSON file
                        try {
                            Scanner quizScanner = new Scanner(quizFile);
                            String quizJSONString = quizScanner.useDelimiter("\\Z").next();
                            quizScanner.close();
                            JSONObject quizJSON = new JSONObject(quizJSONString);
                            JSONArray quizArray = quizJSON.getJSONArray("quiz");

                            // Display each question and prompt for answer
                            for (int i = 0; i < quizArray.length(); i++) {
                                JSONObject questionJSON = quizArray.getJSONObject(i);
                                String prompt = questionJSON.getString("prompt");
                                String type = questionJSON.getString("type");
                                double points = questionJSON.getDouble("points");
                                System.out.println("Question " + (i + 1) + " (" + points + " points): " + prompt);

                                if (type.equals("TF")) {
                                    System.out.print("Enter answer (true or false): ");
                                    String answer = scanner.nextLine();
                                    TrueFalseQuestion question = new TrueFalseQuestion(prompt, points, answer);
                                } else if (type.equals("MC")) {
                                    JSONArray choicesArray = questionJSON.getJSONArray("choices");
                                    String[] choices = new String[choicesArray.length()];
                                    for (int j = 0; j < choicesArray.length(); j++) {
                                        choices[j] = choicesArray.getString(j);
                                    }
                                    System.out.println("Choose the correct answer:");
                                    for (int j = 0; j < choices.length; j++) {
                                        System.out.println((j + 1) + ". " + choices[j]);
                                    }
                                    int choice = Integer.parseInt(scanner.nextLine()) - 1;
                                    MultipleChoiceQuestion question = new MultipleChoiceQuestion(prompt, points, choices[choice], choices);
                                } else if (type.equals("FB")) {
                                    System.out.print("Enter answer: ");
                                    String answer = scanner.nextLine();
                                    FillInTheBlankQuestion question = new FillInTheBlankQuestion(prompt, points, answer);
                                }

                                System.out.println();
                            }
                            System.out.println("Quiz completed. Responses recorded.");
                        } catch (Exception e) {
                            System.out.println("Quiz not found.");
                            System.out.println("_______________________________________________________");
                        }
                        break;

                    //Option 4: Autograding the quizzes in quizToBeGraded folder and exporting results into autoResults folder
                    case 4:
                        System.out.println("\nYou have selected to autograde quizzes");
                        System.out.println("Please put all quizzes to be graded in the quizToBeGraded folder");
                        System.out.println("Please put the answer key in the quizAnswerKey folder");
                        System.out.println("Any files in the autoResult folder will be deleted and replaced with new results");
                        System.out.println("Please press ENTER when the files are in the correct folders");
                        scanner.nextLine();
                        System.out.println("_______________________________________________________");
                        AutoGrader.autograde();
                        System.out.println("_______________________________________________________");
                        break;
                    case 5:
                        System.out.println("You have selected to exit");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            } while (option != 5);
        } catch (NumberFormatException | JSONException e) {
            e.printStackTrace();
        }
    }
}
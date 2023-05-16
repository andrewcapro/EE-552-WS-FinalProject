package edu.stevens.quizdesigner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class Main {
    private static final String QUIZ_FOLDER = "quizzes/";
    private static final String FILE_EXTENSION = ".json";
    public static final Scanner scanner = new Scanner(System.in);

    private static Question createQuestion(String prompt) {
        double points;
        try {
            System.out.print("Enter question point value: ");
            points = scanner.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Invalid point value, stopping question creation");
            return new Question("", 0, "", "");
        } finally {
            scanner.nextLine();
        }

        while (true) {
            System.out.print("Enter question type (TF for true/false, MC for multiple choice, or FB for fill-in-the-blank): ");
            String type = scanner.nextLine().toUpperCase();
            String answer;

            switch (type) {
                case "TF" -> {
                    do {
                        System.out.print("Enter question answer (must be true/false): ");
                        answer = scanner.nextLine().toLowerCase();
                    } while (!answer.equals("true") && !answer.equals("false"));

                    return new TrueFalseQuestion(prompt, points, answer);
                }
                case "MC" -> {
                    System.out.print("Enter comma-separated answer choices: ");
                    String choicesString = scanner.nextLine();
                    List<String> choices = Arrays.asList(choicesString.split(","));

                    do {
                        System.out.print("Enter question answer (must be one of the valid answer choices): ");
                        answer = scanner.nextLine();
                    } while (!choices.contains(answer));

                    return new MultipleChoiceQuestion(prompt, points, answer, choices);
                }
                case "FB" -> {
                    System.out.print("Enter question answer: ");
                    answer = scanner.nextLine();

                    return new FillInTheBlankQuestion(prompt, points, answer);
                }
                default -> System.out.println("Invalid question type. Please try again.");
            }
        }
    }

    private static void createQuiz() {
        List<Question> quizQuestions = new ArrayList<>();

        System.out.println("You have selected to create a quiz");
        System.out.print("Enter quiz name: ");
        String quizName = scanner.nextLine();

        while (true) {
            System.out.print("Enter question prompt (or 'done' to finish): ");
            String prompt = scanner.nextLine();

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
        System.out.println("You have selected to edit question " + questionToEdit + ": " + question.getPrompt());

        System.out.println("\nQuestion point value: " + question.getPoints());
        System.out.print("Enter new question point value (or 'skip' to keep the same): ");
        String points = scanner.nextLine();
        if (!points.equals("skip")) question.setPoints(Double.parseDouble(points));

        System.out.println("Question answer: " + question.getAnswer());
        System.out.print("Enter new question answer (or 'skip' to keep the same): ");
        String answer = scanner.nextLine();
        if (!answer.equals("skip")) question.setAnswer(answer);

        System.out.println("\nWould you like to edit the question type? (Y/N): ");
        String editType = scanner.nextLine();

        if (editType.equalsIgnoreCase("Y")) {
            System.out.println("Question type: " + question.getType());
            System.out.print("Enter new question type (TF, MC, or FB or type anything else to skip): ");
            String type = scanner.nextLine().toUpperCase();

            switch (type) {
                case "TF" -> {
                    System.out.print("Enter new answer (true/false): ");
                    String newAnswer = scanner.nextLine().toLowerCase();
                    if (newAnswer.equals("true") || newAnswer.equals("false")) {
                        System.out.println("Would you like to change the question prompt? (Y/N)");
                        String changePrompt = scanner.nextLine().toUpperCase();

                        if (changePrompt.equals("Y")) {
                            System.out.print("Enter new question prompt: ");
                            String newPrompt = scanner.nextLine();

                            Question newQuestion = new TrueFalseQuestion(
                                    newPrompt, question.getPoints(), newAnswer);
                            quizQuestions.set(questionToEdit - 1, newQuestion);
                        } else {
                            Question newQuestion = new TrueFalseQuestion(
                                    question.getPrompt(), question.getPoints(), newAnswer);
                            quizQuestions.set(questionToEdit - 1, newQuestion);
                        }
                    } else {
                        System.out.println("Invalid answer. Question type will not be changed.");
                    }
                }

                case "MC" -> {
                    System.out.print("Enter new comma-separated answer choices: ");
                    String choicesString = scanner.nextLine();
                    List<String> choices = Arrays.asList(choicesString.split(","));

                    System.out.print("Would you like to change the question prompt? (Y/N)");
                    String changePrompt = scanner.nextLine().toUpperCase();

                    if (changePrompt.equals("Y")) {
                        System.out.print("Enter new prompt: ");
                        String newPrompt = scanner.nextLine();

                        Question newQuestion = new MultipleChoiceQuestion(
                                newPrompt, question.getPoints(), question.getAnswer(), choices);
                        quizQuestions.set(questionToEdit - 1, newQuestion);
                    } else {
                        Question newQuestion = new MultipleChoiceQuestion(
                                question.getPrompt(), question.getPoints(), question.getAnswer(), choices);
                        quizQuestions.set(questionToEdit - 1, newQuestion);
                    }
                }

                case "FB" -> {
                    System.out.print("Would you like to change the question prompt? (Y/N)");
                    String changePrompt = scanner.nextLine().toUpperCase();

                    if (changePrompt.equals("Y")) {
                        System.out.print("Enter new prompt: ");
                        String newPrompt = scanner.nextLine();

                        Question newQuestion = new FillInTheBlankQuestion(
                                newPrompt, question.getPoints(), question.getAnswer());
                        quizQuestions.set(questionToEdit - 1, newQuestion);
                    } else {
                        Question newQuestion = new FillInTheBlankQuestion(
                                question.getPrompt(), question.getPoints(), question.getAnswer());
                        quizQuestions.set(questionToEdit - 1, newQuestion);
                    }
                }

                default -> System.out.println("Question type will not be changed");
            }
        }
    }

    private static void editQuiz() {
        System.out.println("You have selected to edit a quiz");
        System.out.println("Please make sure the quiz you want to edit is in the quizzes folder");
        System.out.println("Please press ENTER to continue");
        scanner.nextLine();

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
        int quizToEdit = scanner.nextInt();
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
                int questionToEdit = scanner.nextInt();
                scanner.nextLine();

                if (questionToEdit == -1) {
                    break;
                } else if (questionToEdit == 0) {
                    System.out.print("Enter question prompt: ");
                    String prompt = scanner.nextLine();
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

    private static void takeQuiz() {
        System.out.println("You have selected to take a quiz");

        File quizFolder = new File(QUIZ_FOLDER);
        File[] quizFiles = quizFolder.listFiles();
        if (quizFiles == null || quizFiles.length == 0) {
            System.out.println("No quizzes found in the 'quizzes' folder.");
            return;
        }

        // Prompt user to select a quiz
        System.out.println("Select a quiz to take:");
        for (int i = 0; i < quizFiles.length; i++) {
            System.out.println((i + 1) + ". " + quizFiles[i].getName().replace(FILE_EXTENSION, ""));
        }
        int quizChoice = scanner.nextInt() - 1;
        scanner.nextLine();
        File quizFile = quizFiles[quizChoice];

        // Prompt user for name
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        File resultsFile = new File("quizToBeGraded/" + name + FILE_EXTENSION);
        // Load quiz from JSON file
        try (FileWriter fileWriter = new FileWriter(resultsFile)) {
            Scanner quizScanner = new Scanner(quizFile);
            String quizJSONString = quizScanner.useDelimiter("\\Z").next();
            quizScanner.close();
            JsonObject quizJSON = JsonParser.parseString(quizJSONString).getAsJsonObject();
            JsonArray questionArray = quizJSON.getAsJsonArray("questions");

            // Create quiz response JSON object
            JsonObject quizResponse = new JsonObject();
            quizResponse.addProperty("name", name);
            JsonArray responsesArray = new JsonArray();

            // Display each question and prompt for answer
            for (int i = 0; i < questionArray.size(); i++) {
                JsonObject questionJSON = questionArray.get(i).getAsJsonObject();
                String type = questionJSON.get("type").getAsString();

                System.out.println("Question " + (i + 1));
                Question question;
                Question response;

                switch (type) {
                    case "TF" -> {
                        question = new Gson().fromJson(questionJSON, TrueFalseQuestion.class);
                        System.out.println(question);
                        System.out.print("Enter answer (true or false): ");
                        String answer = scanner.nextLine();
                        response = new TrueFalseQuestion(question.getPrompt(), question.getPoints(), answer);
                    }
                    case "MC" -> {
                        question = new Gson().fromJson(questionJSON, MultipleChoiceQuestion.class);
                        System.out.println(question);

                        JsonArray choicesArray = questionJSON.getAsJsonArray("choices");
                        List<String> choices = new ArrayList<>();
                        for (int j = 0; j < choicesArray.size(); j++) {
                            choices.add(choicesArray.get(j).getAsString());
                        }
                        System.out.println("Choose the correct answer:");
                        for (int j = 0; j < choices.size(); j++) {
                            System.out.println((j + 1) + ". " + choices.get(j));
                        }

                        int choice = scanner.nextInt() - 1;
                        scanner.nextLine();
                        response = new MultipleChoiceQuestion(
                                question.getPrompt(), question.getPoints(), choices.get(choice), choices);
                    }
                    default -> {
                        question = new Gson().fromJson(questionJSON, FillInTheBlankQuestion.class);
                        System.out.println(question);
                        System.out.print("Enter answer: ");
                        String answer = scanner.nextLine();
                        response = new FillInTheBlankQuestion(question.getPrompt(), question.getPoints(), answer);
                    }
                }

                // Add question response to quiz response array
                JsonObject questionResponse = new JsonObject();
                questionResponse.addProperty("prompt", response.getPrompt());
                questionResponse.addProperty("answer", response.getAnswer());
                questionResponse.addProperty("points", response.getPoints());
                questionResponse.addProperty("type", response.getType());
                responsesArray.add(questionResponse);
                System.out.println();
            }
            // Add quiz response array to quiz response object
            quizResponse.add("questions", responsesArray);

            // Write quiz response JSON object to file
            fileWriter.write(quizResponse.toString());
            System.out.println("Quiz completed. Responses recorded.");
        } catch (IOException e) {
            System.out.println("Error reading quiz or writing submission");
        }
    }

    public static void main(String[] args) {
        try (scanner) {
            System.out.println("----------- Welcome to the Quiz Designer! -----------");
            int option;

            do {
                //Intro selection
                System.out.println("-----------------------------------------------------");

                System.out.println("1) Create a quiz");
                System.out.println("2) Edit a quiz");
                System.out.println("3) Take a quiz");
                System.out.println("4) Autograde quizzes");
                System.out.println("5) Exit");
                System.out.print("Please select an option (1, 2, 3, 4, or 5): ");
                option = scanner.nextInt();
                scanner.nextLine();
                System.out.println("-----------------------------------------------------");

                switch (option) {
                    //Option 1: Creating a quiz and exporting it to the quizzes folder
                    case 1 -> createQuiz();

                    // Option 2: Editing a quiz in the quizzes folder
                    case 2 -> editQuiz();

                    // Option 3: Taking a quiz in the quizzes folder
                    case 3 -> takeQuiz();

                    //Option 4: Autograding the quizzes in quizToBeGraded folder and exporting results into autoResults folder
                    case 4 -> {
                        System.out.println("You have selected to autograde quizzes");
                        System.out.println("Please put all quizzes to be graded in the quizToBeGraded folder");
                        System.out.println("Please put the answer key in the quizzes folder");
                        System.out.println("Please press ENTER when the files are in the correct folders");
                        scanner.nextLine();
                        AutoGrader.autograde();
                    }

                    case 5 -> System.out.println("You have selected to exit");

                    default -> System.out.println("Invalid option. Please try again.");
                }
            } while (option != 5);
        }
    }
}
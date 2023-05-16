package edu.stevens.quizdesigner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
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
        int option;

        do {
            System.out.println("1) Change prompt");
            System.out.println("2) Change points");
            System.out.println("3) Change question type and answer");
            System.out.println("4) Exit");
            System.out.print("Please select an option (1, 2, 3, or 4): ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> {
                    System.out.println("\nCurrent prompt: " + question.getPrompt());
                    System.out.print("Enter new question prompt: ");
                    String newPrompt = scanner.nextLine();
                    question.setPrompt(newPrompt);
                    System.out.println("Prompted successfully changed");
                }
                case 2 -> {
                    System.out.println("\nCurrent point value: " + question.getPoints());
                    System.out.print("Enter new question point value: ");
                    double points = scanner.nextDouble();
                    question.setPoints(points);
                    System.out.println("Points successfully changed");
                }
                case 3 -> {
                    System.out.println("Current question type: " + question.getType());
                    System.out.print("Enter new question type (TF for true/false, MC for multiple choice, or FB for fill-in-the-blank): ");
                    String newType = scanner.nextLine().toUpperCase();
                    String newAnswer;

                    switch (newType) {
                        case "TF" -> {
                            do {
                                System.out.print("Enter new answer (must be true/false): ");
                                newAnswer = scanner.nextLine().toLowerCase();
                            } while (!newAnswer.equals("true") && !newAnswer.equals("false"));

                            Question newQuestion = new TrueFalseQuestion(
                                    question.getPrompt(), question.getPoints(), newAnswer);
                            quizQuestions.set(questionToEdit - 1, newQuestion);
                            System.out.println("Question and answer successfully changed to TF");
                        }

                        case "MC" -> {
                            System.out.print("Enter new comma-separated answer choices: ");
                            String newChoicesStr = scanner.nextLine();
                            List<String> newChoices = Arrays.asList(newChoicesStr.split(","));

                            do {
                                System.out.print("Enter new question answer (must be one of the valid answer choices): ");
                                newAnswer = scanner.nextLine();
                            } while (!newChoices.contains(newAnswer));

                            Question newQuestion = new MultipleChoiceQuestion(
                                    question.getPrompt(), question.getPoints(), newAnswer, newChoices);
                            quizQuestions.set(questionToEdit - 1, newQuestion);
                            System.out.println("Question and answer successfully changed to MC");
                        }

                        case "FB" -> {
                            System.out.print("Enter new answer: ");
                            newAnswer = scanner.nextLine();

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

    private static void exportQuiz() {
        System.out.println("You have selected to export a quiz to a text file");
        File quizFolder = new File(QUIZ_FOLDER);
        File[] quizFiles = quizFolder.listFiles();
        if (quizFiles == null || quizFiles.length == 0) {
            System.out.println("No quizzes found in the 'quizzes' folder.");
            return;
        }

        // Prompt user to select a quiz
        System.out.println("Select a quiz to export:");
        for (int i = 0; i < quizFiles.length; i++) {
            System.out.println((i + 1) + ". " + quizFiles[i].getName().replace(FILE_EXTENSION, ""));
        }
        int quizChoice = scanner.nextInt() - 1;
        scanner.nextLine();
        File quizFile = quizFiles[quizChoice];
        File textFile = new File("autoResult/" + quizFile.getName().replace(FILE_EXTENSION, "") + ".txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(textFile))) {
            Scanner quizScanner = new Scanner(quizFile);
            String quizJSONString = quizScanner.useDelimiter("\\Z").next();
            quizScanner.close();
            JsonObject quizJSON = JsonParser.parseString(quizJSONString).getAsJsonObject();
            JsonArray questionArray = quizJSON.getAsJsonArray("questions");

            writer.write("Name: \n");
            writer.write("Date: \n\n");
            writer.write("Quiz: " + quizFile.getName().replace(FILE_EXTENSION, "") + "\n");

            for (int i = 0; i < questionArray.size(); i++) {
                JsonObject questionJSON = questionArray.get(i).getAsJsonObject();
                String type = questionJSON.get("type").getAsString();
                writer.write("\nQuestion " + (i + 1) + "\n");
                Question question;
                switch (type) {
                    case "TF" -> {
                        question = new Gson().fromJson(questionJSON, TrueFalseQuestion.class);
                        writer.write(question.toString());
                        writer.write("_____\n");
                    }
                    case "MC" -> {
                        question = new Gson().fromJson(questionJSON, MultipleChoiceQuestion.class);
                        writer.write(question.toString());
                    }
                    default -> {
                        question = new Gson().fromJson(questionJSON, FillInTheBlankQuestion.class);
                        writer.write(question.toString());
                        writer.write("______________________________\n");

                    }
                }
            }
            System.out.println("Quiz successfully exported to text file");
        } catch (IOException e) {
            System.out.println("Unable to export to text file");
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
                        String answer;

                        do {
                            System.out.print("Enter answer (must be true/false): ");
                            answer = scanner.nextLine().toLowerCase();
                        } while (!answer.equals("true") && !answer.equals("false"));

                        response = new TrueFalseQuestion(question.getPrompt(), question.getPoints(), answer);
                    }
                    case "MC" -> {
                        question = new Gson().fromJson(questionJSON, MultipleChoiceQuestion.class);
                        System.out.print(question);
                        List<String> choices = ((MultipleChoiceQuestion) question).getChoices();

                        int choice = -1;
                        do {
                            try {
                                System.out.println("Enter answer (must be number corresponding to your answer choice):");
                                choice = scanner.nextInt() - 1;
                            } catch (InputMismatchException | IndexOutOfBoundsException e) {
                                System.out.println("Answer must be one of the corresponding numbers");
                            } finally {
                                scanner.nextLine();
                            }
                        } while (choice < 0 || choice >= choices.size());

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

    private static void autoGrade() {
        System.out.println("You have selected to autograde quizzes");
        System.out.println("Please put all quizzes to be graded in the quizToBeGraded folder");
        System.out.println("Please put the answer key in the quizzes folder");
        System.out.println("Please press ENTER when the files are in the correct folders");
        scanner.nextLine();

        File dir = new File("autoResult");
        File resultFile = new File(dir, "result" + System.currentTimeMillis() + FILE_EXTENSION);

        try (FileWriter writer = new FileWriter(resultFile)) {
            String quizSourceStr = "quizToBeGraded";
            File sourceFolder = new File(quizSourceStr);
            File[] listOfSources = sourceFolder.listFiles();

            String quizAnswersStr = "quizzes";
            File answerFolder = new File(quizAnswersStr);
            File[] listOfAnswers = answerFolder.listFiles();
            String answerKeyStr;

            List<Quiz> resultQuizzes = new ArrayList<>();

            DecimalFormat df = new DecimalFormat("#.##");
            double maxScore = -Double.MAX_VALUE;
            double minScore = Double.MAX_VALUE;
            double averageScore = 0;

            //Getting the answer key from the quizzes folder
            if (listOfAnswers == null || listOfAnswers.length == 0) {
                System.out.println("No answer keys found");
                return;
            }

            System.out.println("Select an answer key:");
            for (int i = 0; i < listOfAnswers.length; i++) {
                System.out.println((i + 1) + ". " + listOfAnswers[i].getName().replace(FILE_EXTENSION, ""));
            }
            int quizChoice = Integer.parseInt(Main.scanner.nextLine()) - 1;
            answerKeyStr = listOfAnswers[quizChoice].getName();

            //Grading all files in the quizToBeGraded folder
            if (listOfSources == null || listOfSources.length == 0) {
                System.out.println("No quizzes to grade found");
                return;
            }

            for (File file : listOfSources) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                FileReader sourceReader = new FileReader(quizSourceStr + "/" + file.getName());
                Quiz currentQuiz = gson.fromJson(sourceReader, Quiz.class);
                List<Question> questions = currentQuiz.getQuestions();

                FileReader answerReader = new FileReader(quizAnswersStr + "/" + answerKeyStr);
                Quiz answerKey = gson.fromJson(answerReader, Quiz.class);
                List<Question> answerList = answerKey.getQuestions();

                double total = 0.0;
                double score = 0.0;

                if (questions.size() != answerList.size()) {
                    System.out.println("Mismatch between quizzes and answer key, stopping autograde");
                    return;
                }

                // Comparing answers for same question and grade the quiz
                for (int i = 0; i < questions.size(); i++) {
                    if (questions.get(i).getPrompt().equals(answerList.get(i).getPrompt())) {
                        if (questions.get(i).getType().equals(answerList.get(i).getType())
                                && (questions.get(i).getAnswer().equals(answerList.get(i).getAnswer()))) {
                            score += answerList.get(i).getPoints();
                        }
                        total += answerList.get(i).getPoints();
                    } else {
                        System.out.println("Mismatch between quizzes and answer key, stopping autograde");
                        return;
                    }
                }
                double finalScore = 0;
                if (total > 0) finalScore = score / total;
                averageScore += finalScore;
                finalScore *= 100;
                finalScore = Double.parseDouble(df.format(finalScore));
                if (finalScore > maxScore) maxScore = finalScore;
                if (finalScore < minScore) minScore = finalScore;

                System.out.println(file.getName() + "'s Score: " + score + "/" + total);
                currentQuiz.setScore(finalScore);
                resultQuizzes.add(currentQuiz);
            }

            averageScore /= listOfSources.length;
            averageScore *= 100;
            averageScore = Double.parseDouble(df.format(averageScore));

            AutogradeResult result = new AutogradeResult(
                    "Autograded quizzes with answers from: " + answerKeyStr,
                    averageScore,
                    maxScore,
                    minScore,
                    resultQuizzes
            );

            System.out.println("Average Score: " + result.averageScore());
            System.out.println("Max Score: " + result.maxScore());
            System.out.println("Min Score: " + result.minScore());

            //writing result file to autoResult
            Gson gson = new GsonBuilder().create();
            writer.write(gson.toJson(result));
            System.out.println("Results exported to result.json in the autoResult folder");
        } catch (IOException e) {
            e.printStackTrace();
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
                System.out.println("3) Export a quiz to a text file");
                System.out.println("4) Take a quiz");
                System.out.println("5) Autograde quizzes");
                System.out.println("6) Exit");
                System.out.print("Please select an option (1, 2, 3, 4, 5, or 6): ");
                option = scanner.nextInt();
                scanner.nextLine();
                System.out.println("-----------------------------------------------------");

                switch (option) {
                    case 1 -> createQuiz();
                    case 2 -> editQuiz();
                    case 3 -> exportQuiz();
                    case 4 -> takeQuiz();
                    case 5 -> autoGrade();
                    case 6 -> System.out.println("You have selected to exit");
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } while (option != 6);
        }
    }
}
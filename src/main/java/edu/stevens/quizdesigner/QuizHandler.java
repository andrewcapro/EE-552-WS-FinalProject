package edu.stevens.quizdesigner;

import com.google.gson.*;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class QuizHandler {
    private static final String QUIZ_FOLDER = "quizzes/";
    private static final String FILE_EXTENSION = ".json";

    private QuizHandler() {}

    public static void exportQuiz() {
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
        int quizChoice = Main.scanner.nextInt() - 1;
        Main.scanner.nextLine();
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

    public static void takeQuiz() {
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
        int quizChoice = Main.scanner.nextInt() - 1;
        Main.scanner.nextLine();
        File quizFile = quizFiles[quizChoice];

        // Prompt user for name
        System.out.print("Enter your name: ");
        String name = Main.scanner.nextLine();

        // Load quiz from JSON file
        String quizJSONString;
        try (Scanner quizScanner = new Scanner(quizFile)) {
            quizJSONString = quizScanner.useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            System.out.println("Could not read quiz file");
            return;
        }

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
                        answer = Main.scanner.nextLine().toLowerCase();
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
                            choice = Main.scanner.nextInt() - 1;
                        } catch (InputMismatchException | IndexOutOfBoundsException e) {
                            System.out.println("Answer must be one of the corresponding numbers");
                        } finally {
                            Main.scanner.nextLine();
                        }
                    } while (choice < 0 || choice >= choices.size());

                    response = new MultipleChoiceQuestion(
                            question.getPrompt(), question.getPoints(), choices.get(choice), choices);
                }
                default -> {
                    question = new Gson().fromJson(questionJSON, FillInTheBlankQuestion.class);
                    System.out.println(question);
                    System.out.print("Enter answer: ");
                    String answer = Main.scanner.nextLine();
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
        File resultsFile = new File("quizToBeGraded/" + name + FILE_EXTENSION);
        try (FileWriter fileWriter = new FileWriter(resultsFile)) {
            fileWriter.write(quizResponse.toString());
        } catch (IOException e) {
            System.out.println("Error reading quiz or writing submission");
        }
        System.out.println("Quiz completed. Responses recorded.");
    }

    public static void autoGrade() {
        System.out.println("You have selected to autograde quizzes");
        System.out.println("Please put all quizzes to be graded in the quizToBeGraded folder");
        System.out.println("Please put the answer key in the quizzes folder");
        System.out.println("Please press ENTER when the files are in the correct folders");
        Main.scanner.nextLine();

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

}

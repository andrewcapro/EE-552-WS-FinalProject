package edu.stevens.quizdesigner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AutoGrader {
    private AutoGrader() {
    }

    public static void autograde() {
        try {
            Path del = Paths.get("autoResult/result.json");
            Files.delete(del);
            String quizSource = "quizToBeGraded";
            String quizAnswers = "quizzes";

            File folder = new File(quizSource);
            File[] listOfFiles = folder.listFiles();

            File folder2 = new File(quizAnswers);
            File[] listOfFiles2 = folder2.listFiles();

            AutogradeResult result = new AutogradeResult();
            List<Quiz> resultQuizzes = new ArrayList<>();

            double maxScore = 0;
            double minScore = 0;
            double averageScore = 0;
            String answerKey = "";

            //Getting the answer key from the quizzes folder
            for (File file2 : listOfFiles2) {
                if (file2.isFile()) {
                    answerKey = file2.getName();
                }
            }

            result.setName("Autograded quizzes with answers from: " + answerKey);

            //Grading all files in the quizToBeGraded folder
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    FileReader reader = new FileReader(quizSource + "/" + file.getName());
                    Quiz quizS = gson.fromJson(reader, Quiz.class);
                    List<Question> quiz = quizS.getQuiz();

                    FileReader reader2 = new FileReader(quizAnswers + "/" + answerKey);
                    Quiz quizA = gson.fromJson(reader2, Quiz.class);
                    List<Question> answers = quizA.getQuiz();

                    int total = 0;
                    double score = 0.0;

                    // Comparing answers for same question and grade the quiz
                    for (int i = 0; i < quiz.size(); i++) {
                        if (quiz.get(i).getPrompt().equals(answers.get(i).getPrompt())) {
                            if (quiz.get(i).getType().equals(answers.get(i).getType()) && (quiz.get(i).getAnswer().equals(answers.get(i).getAnswer()))) {
                                    score += answers.get(i).getPoints();
                            }
                            total += answers.get(i).getPoints();
                        }
                    }
                    averageScore += score / total;
                    if (score / total > maxScore) {
                        maxScore = 100 * score / total;
                    }

                    if (score / total < minScore) {
                        minScore = 100 * score / total;
                    }
                    System.out.println(quizS.getName() + "'s Score: " + score + "/" + total);
                    quizS.setScore(Math.round(10000 * score / total) / 100);
                    resultQuizzes.add(quizS);
                }
            }

            averageScore = Math.round(100 * averageScore / listOfFiles.length * 100) / 100.0;
            result.setAverageScore(Math.round(averageScore * 100) / 100.0);
            result.setMaxScore(Math.round(maxScore * 100) / 100.0);
            result.setMinScore(Math.round(minScore * 100) / 100.0);
            result.setQuizs(resultQuizzes);

            System.out.println("Average Score: " + result.getAverageScore());
            System.out.println("Max Score: " + result.getMaxScore());
            System.out.println("Min Score: " + result.getMinScore());

            //writing result file to autoResult
            GsonBuilder build = new GsonBuilder();
            Gson gson = build.create();
            File dir = new File("autoResult");
            File file = new File(dir, "result.json");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(gson.toJson(result));
                System.out.println("Results exported to result.json in the autoResult folder");
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

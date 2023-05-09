package edu.stevens.quizdesigner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AutoGrader {


    public static void autograde() {
        try {
            File del = new File("autoResult/result.json");
            if (del.delete()) {
                System.out.println("Deleted the file: " + del.getName());
            } else {
                System.out.println();
            }
            String quizSource = "quizToBeGraded";
            String quizAnswers = "quizzes";

            File folder = new File(quizSource);
            File[] listOfFiles = folder.listFiles();

            File folder2 = new File(quizAnswers);
            File[] listOfFiles2 = folder2.listFiles();

            AutogradeResult result = new AutogradeResult();
            ArrayList<Quiz> resultQuizzes = new ArrayList<Quiz>();

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

            result.name = "Autograded quizzes with answers from: " + answerKey;

            //Grading all files in the quizToBeGraded folder
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    FileReader reader = new FileReader(quizSource + "/" + file.getName());
                    Quiz quizS = gson.fromJson(reader, Quiz.class);
                    ArrayList<Question> quiz = quizS.quiz;

                    FileReader reader2 = new FileReader(quizAnswers + "/" + answerKey);
                    Quiz quizA = gson.fromJson(reader2, Quiz.class);
                    ArrayList<Question> answers = quizA.quiz;

                    int total = 0;
                    double score = 0.0;

                    // Comparing answers for same question and grade the quiz
                    for (int i = 0; i < quiz.size(); i++) {
                        if (quiz.get(i).prompt.equals(answers.get(i).prompt)) {
                            if (quiz.get(i).type.equals(answers.get(i).type)) {
                                if (quiz.get(i).answer.equals(answers.get(i).answer)) {
                                    score += answers.get(i).points;
                                }
                            }

                            total += answers.get(i).points;
                        }
                    }
                    averageScore += score / total;
                    if (score / total > maxScore) {
                        maxScore = 100 * score / total;
                    }

                    if (score / total < minScore) {
                        minScore = 100 * score / total;
                    }
                    System.out.println(quizS.name + "'s Score: " + score + "/" + total);
                    quizS.score = Math.round(10000 * score / total) / 100;
                    resultQuizzes.add(quizS);
                }
            }

            averageScore = Math.round(100 * averageScore / listOfFiles.length * 100) / 100.0;
            result.averageScore = Math.round(averageScore * 100) / 100.0;
            result.maxScore = Math.round(maxScore * 100) / 100.0;
            result.minScore = Math.round(minScore * 100) / 100.0;
            result.quizs = resultQuizzes;

            System.out.println("Average Score: " + result.averageScore);
            System.out.println("Max Score: " + result.maxScore);
            System.out.println("Min Score: " + result.minScore);

            //writing result file to autoResult
            GsonBuilder build = new GsonBuilder();
            Gson gson = build.create();
            File dir = new File("autoResult");
            File file = new File(dir, "result.json");
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(result));
            writer.close();
            System.out.println("Results exported to result.json in the autoResult folder");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

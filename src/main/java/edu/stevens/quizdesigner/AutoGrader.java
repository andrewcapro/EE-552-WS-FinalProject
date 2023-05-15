package edu.stevens.quizdesigner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AutoGrader {
    private AutoGrader() {
    }

    public static void autograde() {
        File dir = new File("autoResult");
        File resultFile = new File(dir, "result" + System.currentTimeMillis() + ".json");

        try (FileWriter writer = new FileWriter(resultFile)) {
            String quizSourceStr = "quizToBeGraded";
            File sourceFolder = new File(quizSourceStr);
            File[] listOfSources = sourceFolder.listFiles();

            String quizAnswersStr = "quizzes";
            File answerFolder = new File(quizAnswersStr);
            File[] listOfAnswers = answerFolder.listFiles();
            String answerKeyStr = "";

            List<Quiz> resultQuizzes = new ArrayList<>();

            DecimalFormat df = new DecimalFormat("#.##");
            double maxScore = -Double.MAX_VALUE;
            double minScore = Double.MAX_VALUE;
            double averageScore = 0;

            //Getting the answer key from the quizzes folder
            if (listOfAnswers != null) {
                for (File file : listOfAnswers) {
                    if (file.isFile()) answerKeyStr = file.getName();
                }
            } else {
                System.out.println("No answer key found");
                return;
            }

            //Grading all files in the quizToBeGraded folder
            if (listOfSources != null) {
                for (File file : listOfSources) {
                    if (file.isFile()) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        FileReader sourceReader = new FileReader(quizSourceStr + "/" + file.getName());
                        Quiz currentQuiz = gson.fromJson(sourceReader, Quiz.class);
                        List<Question> questions = currentQuiz.getQuestions();

                        FileReader answerReader = new FileReader(quizAnswersStr + "/" + answerKeyStr);
                        Quiz answerKey = gson.fromJson(answerReader, Quiz.class);
                        List<Question> answerList = answerKey.getQuestions();

                        double total = 0.0;
                        double score = 0.0;

                        // Comparing answers for same question and grade the quiz
                        for (int i = 0; i < questions.size(); i++) {
                            if (questions.get(i).getPrompt().equals(answerList.get(i).getPrompt())) {
                                if (questions.get(i).getType().equals(answerList.get(i).getType())
                                        && (questions.get(i).getAnswer().equals(answerList.get(i).getAnswer()))) {
                                    score += answerList.get(i).getPoints();
                                }
                                total += answerList.get(i).getPoints();
                            }
                        }
                        double finalScore = 0;
                        if (total > 0) finalScore = score / total;
                        averageScore += finalScore;
                        finalScore *= 100;
                        finalScore = Double.parseDouble(df.format(finalScore));
                        if (finalScore > maxScore) maxScore = finalScore;
                        if (finalScore < minScore) minScore = finalScore;

                        System.out.println(currentQuiz.getName() + "'s Score: " + score + "/" + total);
                        currentQuiz.setScore(finalScore);
                        resultQuizzes.add(currentQuiz);
                    }
                }
            } else {
                System.out.println("No quizzes to be graded found");
                return;
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

package edu.stevens.quizdesigner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
public class AutoGrader {
    
    
    public static void autograde(String quizSource, String quizAnswers) {
        try{

            File folder = new File(quizSource);
            File[] listOfFiles = folder.listFiles();
            AutogradeResult result = new AutogradeResult();
            ArrayList<Quiz> resultQuizs = new ArrayList<Quiz>();

            double maxScore = 0;
            double minScore = 0;
            double averageScore = 0;


            result.name = "Quiz with answers from: " + quizAnswers;
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    FileReader reader = new FileReader(quizSource + "/"+file.getName());
                    Quiz quizS = gson.fromJson(reader, Quiz.class);
                    ArrayList<Question> quiz = quizS.quiz;


                    FileReader reader2 = new FileReader(quizAnswers);
                    Quiz quizA = gson.fromJson(reader2, Quiz.class);
                    ArrayList<Question> answers = quizA.quiz;

                    int total= 0;
                    Double score = 0.0;
                    for(int i = 0; i < quiz.size(); i++){
                        if (quiz.get(i).prompt.equals(answers.get(i).prompt)){
                            if (quiz.get(i).answer.equals(answers.get(i).answer)){
                                score += answers.get(i).points;
                            }
                            total += answers.get(i).points;
                        }
                    }
                    averageScore += score/total;
                    if (score/total > maxScore){
                        maxScore = 100*score/total;
                    }

                    if (score/total < minScore){
                        minScore = 100*score/total;
                    }
                    System.out.println(quizS.name + "'s Score: " + score + "/" + total);
                    quizS.score = Math.round(10000*score/total)/100;
                    resultQuizs.add(quizS);
                }
            }



            averageScore = Math.round(100*averageScore / listOfFiles.length*100)/100.0;
            result.avergageScore = Math.round(averageScore*100)/100.0;
            result.maxScore = Math.round(maxScore*100)/100.0;
            result.minScore = Math.round(minScore*100)/100.0;
            result.quizs = resultQuizs;

            System.out.println("Average Score: " + averageScore);
            System.out.println("Max Score: " + maxScore);
            System.out.println("Min Score: " + minScore);
            

            GsonBuilder build = new GsonBuilder();
            Gson gson = build.create();
            File dir = new File("autoResult");
            File file = new File(dir, "result.json");
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(result));
            writer.close();


        }
        catch(Exception e){
            System.out.println(e);
        }



    }
}

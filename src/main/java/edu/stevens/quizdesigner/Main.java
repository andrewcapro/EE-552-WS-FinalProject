package edu.stevens.quizdesigner;

import java.util.Scanner;

public class Main {
    public static final Scanner scanner = new Scanner(System.in);

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
                    case 1 -> QuizDesigner.createQuiz();
                    case 2 -> QuizDesigner.editQuiz();
                    case 3 -> QuizHandler.exportQuiz();
                    case 4 -> QuizHandler.takeQuiz();
                    case 5 -> QuizHandler.autoGrade();
                    case 6 -> System.out.println("You have selected to exit");
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } while (option != 6);
        }
    }
}
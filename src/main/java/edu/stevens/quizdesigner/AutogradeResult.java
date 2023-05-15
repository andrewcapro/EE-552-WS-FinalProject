package edu.stevens.quizdesigner;

import java.util.List;

public record AutogradeResult(
        String name,
        double averageScore,
        double maxScore,
        double minScore,
        List<Quiz>quizzes
) {
}

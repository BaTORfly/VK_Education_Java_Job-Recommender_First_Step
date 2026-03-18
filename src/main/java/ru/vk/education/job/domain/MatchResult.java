package ru.vk.education.job.domain;

import java.util.Comparator;

/**
 * Хранение пары (Вакансия + Score) для сортировки результатов рекомендации.
 */
public record MatchResult(
        Job job,
        double score
) {
    public static Comparator<MatchResult> byScoreDescending() {
        return Comparator.comparingDouble(MatchResult::score).reversed();
    }
}

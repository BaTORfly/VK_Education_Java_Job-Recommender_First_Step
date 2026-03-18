package ru.vk.education.job.domain;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Хранение данных пользователя и расчет соответствия вакансии.
 */
public record User(
        String name,
        List<String> skills,
        int exp
) {
    public static User create(String name, List<String> rawSkills, int exp) {
        List<String> normalizedSkills = rawSkills.stream()
                .map(String::toLowerCase)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return new User(name, normalizedSkills, exp);
    }

    public double calculateMatchScore(Job job) {
        long matches = this.skills.stream()
                .filter(job.tags()::contains)
                .count();

        double score = (double) matches;

        if (this.exp < job.requiredExp()) {
            score = score / 2.0;
        }

        return score;
    }

    public String getSkillsAsString() {
        return String.join(",", this.skills);
    }
}


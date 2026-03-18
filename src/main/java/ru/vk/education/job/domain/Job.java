package ru.vk.education.job.domain;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Хранение данных вакансии
 */
public record Job(
        String title,
        String company,
        List<String> tags,
        int requiredExp
) {
    public static Job create(String title, String company, List<String> rawTags, int requiredExp) {
        List<String> normalizedTags = rawTags.stream()
                .map(String::toLowerCase)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return new Job(title, company, normalizedTags, requiredExp);
    }

    public String getFormattedOutput() {
        return String.format("%s at %s", this.title, this.company);
    }
}


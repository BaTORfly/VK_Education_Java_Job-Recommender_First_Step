package ru.vk.education.job.service;

import ru.vk.education.job.domain.Job;
import ru.vk.education.job.domain.MatchResult;
import ru.vk.education.job.domain.User;
import ru.vk.education.job.repo.InMemoryRepository;

import java.util.List;
import java.util.Optional;

/**
 * Поиск и ранжирование вакансий для пользователя
 */
public class RecommendationService {

    private final InMemoryRepository repository;

    private static final int MAX_RECOMMENDATIONS = 2;

    public RecommendationService(InMemoryRepository repository) {
        this.repository = repository;
    }

    /**
     * Находит наиболее релевантные вакансии для пользователя.
     * @param username имя пользователя
     * @return список вакансий (не более 2), отсортированных по релевантности
     */
    public List<Job> findTopMatches(String username) {
        Optional<User> userOpt = repository.getUserByName(username);
        if (userOpt.isEmpty()) {
            return List.of();
        }

        User user = userOpt.get();

        List<Job> allJobs = repository.getAllJobs().stream().toList();
        if (allJobs.isEmpty()) {
            return List.of();
        }

        List<MatchResult> matches = allJobs.stream()
                .map(job -> new MatchResult(job, user.calculateMatchScore(job)))
                .toList();

        return matches.stream()
                .sorted(MatchResult.byScoreDescending())
                .limit(MAX_RECOMMENDATIONS)
                .map(MatchResult::job)
                .toList();
    }

    /**
     * Проверка: существует ли пользователь в системе.
     * Вынесено для удобства CLI (чтобы не дублировать логику).
     */
    public boolean userExists(String username) {
        return repository.hasUser(username);
    }
}

package ru.vk.education.job.service;

import ru.vk.education.job.domain.Job;
import ru.vk.education.job.domain.User;
import ru.vk.education.job.repo.InMemoryRepository;

import java.util.Collection;
import java.util.List;

public class JobRecommendationBackgroundTask implements Runnable {
    private final InMemoryRepository repo;
    private final RecommendationService recommendationService;

    public JobRecommendationBackgroundTask(InMemoryRepository repo,
                                           RecommendationService recommendationService) {
        this.repo = repo;
        this.recommendationService = recommendationService;
    }

    @Override
    public void run() {
        try {
            Collection<User> users;
            synchronized (repo) {
                users = repo.getAllUsers();
            }

            if (users.isEmpty()) {
                return;
            }

            for (User user : users) {
                List<Job> topMatches = recommendationService.findTopMatches(user.name());
                if (!topMatches.isEmpty()) {
                    Job bestJob = topMatches.get(0);
                    System.out.printf("%s, лучшее предложение — %s в %s%n",
                            user.name(), bestJob.title(), bestJob.company());
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка в фоновом процессе рекомендаций: " + e.getMessage());
        }
    }
}

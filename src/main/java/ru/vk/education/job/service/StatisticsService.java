package ru.vk.education.job.service;

import ru.vk.education.job.domain.Job;
import ru.vk.education.job.domain.User;
import ru.vk.education.job.repo.InMemoryRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for calculating statistics
 */
public class StatisticsService {
    private final InMemoryRepository repository;

    public StatisticsService(InMemoryRepository inMemoryRepository) {
        this.repository = inMemoryRepository;
    }

    public List<Job> findJobsWithMinExp(int minExp) {
        return repository.getAllJobs().stream()
                .filter(job -> job.requiredExp() >= minExp)
                .sorted(Comparator.comparing(Job::title))
                .toList();
    }

    public List<User> findUsersWithMinMatches(int minMatches) {
        return repository.getAllUsers().stream()
                .filter(user -> repository.getAllJobs().stream()
                        .filter(job -> user.calculateMatchScore(job) > 0)
                        .count() >= minMatches)
                .sorted(Comparator.comparing(User::name))
                .toList();
    }

    public List<String> findTopSkills(int n) {
        return repository.getAllUsers().stream()
                .flatMap(user -> user.skills().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(n)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }
}

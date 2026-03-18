package ru.vk.education.job.cli;

import ru.vk.education.job.domain.Job;
import ru.vk.education.job.domain.User;
import ru.vk.education.job.repo.InMemoryRepository;
import ru.vk.education.job.service.RecommendationService;

import java.util.Arrays;
import java.util.List;

/**
 * Обработка команд после парсинга (связка между CLI и Service)
 */
public class CliHandler {

    private final InMemoryRepository repository;
    private final RecommendationService recommendationService;

    public CliHandler(InMemoryRepository repository, RecommendationService recommendationService) {
        this.repository = repository;
        this.recommendationService = recommendationService;
    }

    /**
     * Обрабатывает распарсенную команду.
     * @return true если нужно продолжить работу, false если exit
     */
    public boolean handle(ParsedCommand cmd) {
        return switch (cmd.type()) {
            case USER -> handleUser(cmd);
            case USER_LIST -> handleUserList();
            case JOB -> handleJob(cmd);
            case JOB_LIST -> handleJobList();
            case SUGGEST -> handleSuggest(cmd);
            case EXIT -> false;
            case UNKNOWN -> true;
        };
    }

    private boolean handleUser(ParsedCommand cmd) {
        if (cmd.primaryArg() == null || !cmd.hasFlag("skills") || !cmd.hasFlag("exp")) {
            return true; // Неверный формат — игнорируем
        }

        List<String> skills = Arrays.asList(cmd.getFlag("skills", "").split(","));
        int exp = parseInt(cmd.getFlag("exp", "0"));

        User user = User.create(cmd.primaryArg(), skills, exp);
        repository.addUser(user);

        return true;
    }

    private boolean handleUserList() {
        OutputFormatter.printUsers(repository.getAllUsers());
        return true;
    }

    private boolean handleJob(ParsedCommand cmd) {
        if (cmd.primaryArg() == null || !cmd.hasFlag("company") ||
                !cmd.hasFlag("tags") || !cmd.hasFlag("exp")) {
            return true;
        }

        List<String> tags = Arrays.asList(cmd.getFlag("tags", "").split(","));
        int exp = parseInt(cmd.getFlag("exp", "0"));

        Job job = Job.create(cmd.primaryArg(), cmd.getFlag("company", ""), tags, exp);
        repository.addJob(job);

        return true;
    }

    private boolean handleJobList() {
        OutputFormatter.printJobs(repository.getAllJobs());
        return true;
    }

    private boolean handleSuggest(ParsedCommand cmd) {
        if (cmd.primaryArg() == null) {
            return true;
        }

        List<Job> recommendations = recommendationService.findTopMatches(cmd.primaryArg());
        OutputFormatter.printRecommendations(recommendations);

        return true;
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

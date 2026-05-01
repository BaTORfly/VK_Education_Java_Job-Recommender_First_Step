package ru.vk.education.job.cli;

import ru.vk.education.job.cli.enums.Command;
import ru.vk.education.job.domain.Job;
import ru.vk.education.job.domain.User;
import ru.vk.education.job.repo.InMemoryRepository;
import ru.vk.education.job.service.FileService;
import ru.vk.education.job.service.RecommendationService;
import ru.vk.education.job.service.StatisticsService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Обработка команд после парсинга (связка между CLI и Service)
 */
public class CliHandler {

    private final InMemoryRepository repository;
    private final RecommendationService recommendationService;
    private final FileService fileService; // added for history command
    private final StatisticsService statisticsService;

    public CliHandler(InMemoryRepository repository,
                      RecommendationService recommendationService,
                      FileService fileService,
                      StatisticsService statisticsService) {
        this.repository = repository;
        this.recommendationService = recommendationService;
        this.fileService = fileService;
        this.statisticsService = statisticsService;
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
            case HISTORY -> handleHistory();
            case STAT -> handleStat(cmd);
            case EXIT -> false;
            case UNKNOWN -> true;
        };
    }

    private boolean handleStat(ParsedCommand cmd) {
        if (cmd.hasFlag("exp")){
            int minExp = parseInt(cmd.getFlag("exp", "0"));
            List<Job> jobs = statisticsService.findJobsWithMinExp(minExp);
            OutputFormatter.printJobs(jobs);
        } else if (cmd.hasFlag("match")) {
            int minMatches = parseInt(cmd.getFlag("match", "0"));
            List<User> users = statisticsService.findUsersWithMinMatches(minMatches);
            OutputFormatter.printUsers(users);
        } else if (cmd.hasFlag("top-skills")) {
            int n = parseInt(cmd.getFlag("top-skills", "0"));
            List<String> skills = statisticsService.findTopSkills(n);
            skills.forEach(System.out::println);
        }
        return true;
    }

    /**
     * Тихое выполнение при загрузке из файла.
     * Выполнение только создания сущностей (Пользователь, Вакансия)
     * @param cmd
     */
    public void handleSilent(ParsedCommand cmd){
        if (cmd.type() == Command.USER){
            handleUser(cmd);
        } else if (cmd.type() == Command.JOB){
            handleJob(cmd);
        }
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
        OutputFormatter.printJobs(repository.getAllJobs().stream().sorted(Comparator.comparing(Job::title)).toList());
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

    private boolean handleHistory(){
        List<String> commands = fileService.loadCommands();
        for (String command : commands) {
            System.out.println(command);
        }
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

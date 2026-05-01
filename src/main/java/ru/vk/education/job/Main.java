package ru.vk.education.job;

import ru.vk.education.job.cli.CliHandler;
import ru.vk.education.job.cli.CommandParser;
import ru.vk.education.job.cli.ParsedCommand;
import ru.vk.education.job.cli.enums.Command;
import ru.vk.education.job.repo.InMemoryRepository;
import ru.vk.education.job.service.FileService;
import ru.vk.education.job.service.JobRecommendationBackgroundTask;
import ru.vk.education.job.service.RecommendationService;
import ru.vk.education.job.service.StatisticsService;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {     
    private static final long TASK_INTERVAL_SECONDS = 60;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 60;

    public static void main(String[] args) {
        InMemoryRepository repository = new InMemoryRepository();
        RecommendationService recommendationService = new RecommendationService(repository);
        FileService fileService = new FileService();
        StatisticsService statisticsService = new StatisticsService(repository);

        CliHandler cliHandler = new CliHandler(repository, recommendationService, fileService, statisticsService);

        // Initializing the scheduler and background task
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        JobRecommendationBackgroundTask backgroundTask = new JobRecommendationBackgroundTask(repository,
                recommendationService);

        // Downloading and silently executing commands from a file
        List<String> savedCommands = fileService.loadCommands();
        for (String line : savedCommands) {
            ParsedCommand cmd = CommandParser.parse(line);
            if (cmd.type() == Command.USER || cmd.type() == Command.JOB) {
                cliHandler.handleSilent(cmd);
            }
        }

        // Starting a periodic task execution
        scheduler.scheduleAtFixedRate(backgroundTask, 0, TASK_INTERVAL_SECONDS, TimeUnit.SECONDS);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            ParsedCommand cmd = CommandParser.parse(line);

            boolean shouldContinue = cliHandler.handle(cmd);

            if (shouldContinue && cmd.type() != Command.EXIT) {
                fileService.saveCommand(line);
            }

            if (!shouldContinue) {
                break;
            }
        }

        // smooth completion ExecutorService
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }


        scanner.close();
        System.exit(0);
    }
}
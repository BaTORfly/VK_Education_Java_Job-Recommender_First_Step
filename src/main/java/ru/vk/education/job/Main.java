package ru.vk.education.job;

import ru.vk.education.job.cli.CliHandler;
import ru.vk.education.job.cli.CommandParser;
import ru.vk.education.job.cli.ParsedCommand;
import ru.vk.education.job.repo.InMemoryRepository;
import ru.vk.education.job.service.RecommendationService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        InMemoryRepository repository = new InMemoryRepository();
        RecommendationService recommendationService = new RecommendationService(repository);
        CliHandler cliHandler = new CliHandler(repository, recommendationService);

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            ParsedCommand cmd = CommandParser.parse(line);

            boolean shouldContinue = cliHandler.handle(cmd);
            if (!shouldContinue) {
                break;
            }
        }

        scanner.close();
        System.exit(0);
    }
}
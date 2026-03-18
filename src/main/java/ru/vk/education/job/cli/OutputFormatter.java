package ru.vk.education.job.cli;

import ru.vk.education.job.domain.Job;
import ru.vk.education.job.domain.User;

import java.util.Collection;

/**
 * Строгое форматирование вывода, согласно ТЗ
 */
public class OutputFormatter {

    public static String formatUser(User user) {
        return String.format("%s %s %d",
                user.name(),
                user.getSkillsAsString(),
                user.exp()
        );
    }


    public static String formatJob(Job job) {
        return job.getFormattedOutput();
    }


    public static void printUsers(Collection<User> users) {
        for (User user : users) {
            System.out.println(formatUser(user));
        }
    }

    public static void printJobs(Collection<Job> jobs) {
        for (Job job : jobs) {
            System.out.println(formatJob(job));
        }
    }

    public static void printRecommendations(Collection<Job> jobs) {
        printJobs(jobs);
    }
}

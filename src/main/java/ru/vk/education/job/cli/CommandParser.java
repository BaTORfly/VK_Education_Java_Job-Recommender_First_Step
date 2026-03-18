package ru.vk.education.job.cli;

import ru.vk.education.job.cli.enums.Command;

import java.util.HashMap;
import java.util.Map;

/**
 * Парсинг входной строки в структурированную команду
 */
public class CommandParser {

    /**
     * Парсит входную строку в структурированную команду.
     * @param line входная строка (например: "user alice --skills=java,ml --exp=2")
     * @return ParsedCommand с типом команды и аргументами
     */
    public static ParsedCommand parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            return new ParsedCommand(Command.UNKNOWN, null, Map.of());
        }

        String trimmed = line.trim();
        String[] parts = trimmed.split("\\s+");

        if (parts.length == 0) {
            return new ParsedCommand(Command.UNKNOWN, null, Map.of());
        }

        String commandWord = parts[0].toLowerCase();
        Command type = parseCommandType(commandWord);


        if (type == Command.EXIT) {
            return new ParsedCommand(Command.EXIT, null, Map.of());
        }

        if (type == Command.USER_LIST || type == Command.JOB_LIST) {
            return new ParsedCommand(type, null, Map.of());
        }


        String primaryArg = (parts.length > 1 && !parts[1].startsWith("--"))
                ? parts[1]
                : null;

        Map<String, String> flags = new HashMap<>();
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].startsWith("--")) {
                parseFlag(parts[i], flags);
            }
        }

        return new ParsedCommand(type, primaryArg, flags);
    }

    private static Command parseCommandType(String word) {
        return switch (word) {
            case "user" -> Command.USER;
            case "user-list" -> Command.USER_LIST;
            case "job" -> Command.JOB;
            case "job-list" -> Command.JOB_LIST;
            case "suggest" -> Command.SUGGEST;
            case "exit" -> Command.EXIT;
            default -> Command.UNKNOWN;
        };
    }

    private static void parseFlag(String flagPart, Map<String, String> flags) {

        int eqIndex = flagPart.indexOf('=');
        if (eqIndex > 2) {
            String key = flagPart.substring(2, eqIndex);
            String value = flagPart.substring(eqIndex + 1);
            flags.put(key, value);
        }
    }
}

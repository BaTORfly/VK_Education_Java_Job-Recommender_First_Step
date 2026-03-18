package ru.vk.education.job.cli;

import ru.vk.education.job.cli.enums.Command;

import java.util.Map;

/**
 * Хранение результатов парсинга команды (тип + аргументы)
 */
public record ParsedCommand(
        Command type,
        String primaryArg,
        Map<String, String> flags
) {

    public String getFlag(String key, String defaultValue) {
        return flags.getOrDefault(key, defaultValue);
    }

    public boolean hasFlag(String key) {
        return flags.containsKey(key);
    }
}
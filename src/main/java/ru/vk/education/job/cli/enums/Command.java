package ru.vk.education.job.cli.enums;

/**
 * Типизация команд для безопасной обработки
 */
public enum Command {
    USER,           // user <name> --skills=... --exp=...
    USER_LIST,      // user-list
    JOB,            // job <title> --company=... --tags=... --exp=...
    JOB_LIST,       // job-list
    SUGGEST,        // suggest <username>
    EXIT,           // exit
    UNKNOWN
}

package ru.vk.education.job.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

/**
 * Чтение и запись команд в файл commands.log
 * Reading and writing commands to file commands.log
 */
public class FileService {
    private static final String FILE_NAME = "commands.log";
    private static final String EOL = System.lineSeparator();
    private final Path filePath;

    public FileService() {
        this.filePath = Paths.get(FILE_NAME);
    }

    /**
     * Save the command to file
     * @param command full command line (ex: "user alice --skills=java --exp=2")
     */
    public void saveCommand(String command){
        try {
            Files.writeString(
                    filePath,
                    command + EOL,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Ошибка записи в файл: " + e.getMessage());
        }
    }

    /**
     * Download all the commands from file
     * @return list of lines or empty list if file isn't exist
     */
    public List<String> loadCommands() {
        if(!Files.exists(filePath)) {
            return Collections.emptyList();
        }
        try{
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла истории" + e.getMessage());
            return Collections.emptyList();
        }
    }
}

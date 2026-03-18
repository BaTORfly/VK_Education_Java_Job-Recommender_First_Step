package ru.vk.education.job.repo;

import ru.vk.education.job.domain.Job;
import ru.vk.education.job.domain.User;

import java.util.*;


/**
 * Хранение и управление коллекциями пользователей и вакансий.
 * Соответствуя требованию тз - Повторное создание не должно приводить к перезаписи.
 */
public class InMemoryRepository {

    private final Map<String, User> users = new LinkedHashMap<>();

    private final Map<String, Job> jobs = new LinkedHashMap<>();

    /**
     * Добавляет пользователя в систему.
     * Если пользователь с таким именем уже существует — не перезаписывает.
     * @return true если пользователь добавлен, false если уже существует
     */
    public boolean addUser(User user) {
        if (users.containsKey(user.name())) {
            return false;
        }
        users.put(user.name(), user);
        return true;
    }

    /**
     * Добавляет вакансию в систему.
     * Если вакансия с таким названием уже существует — не перезаписывает.
     * @return true если вакансия добавлена, false если уже существует
     */
    public boolean addJob(Job job) {
        if (jobs.containsKey(job.title())) {
            return false;
        }
        jobs.put(job.title(), job);
        return true;
    }

    /**
     * Получает пользователя по имени.
     * @return Optional с пользователем или пустой, если не найден
     */
    public Optional<User> getUserByName(String name) {
        return Optional.ofNullable(users.get(name));
    }

    /**
     * Получает всех пользователей (для команды user-list).
     * Возвращаем копию коллекции, чтобы нельзя было изменить внутреннее состояние.
     */
    public Collection<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    /**
     * Получает все вакансии (для команды job-list и suggest).
     * Возвращаем копию коллекции для безопасности.
     */
    public Collection<Job> getAllJobs() {
        return List.copyOf(jobs.values());
    }

    /**
     * Проверка наличия пользователя (может пригодиться для валидации).
     */
    public boolean hasUser(String name) {
        return users.containsKey(name);
    }

    /**
     * Проверка наличия вакансии.
     */
    public boolean hasJob(String title) {
        return jobs.containsKey(title);
    }
}

package com.dubrovsky.task.restful.service;

import com.dubrovsky.task.restful.dto.TaskDto;
import com.dubrovsky.task.restful.exception.TaskNotFoundException;

import java.util.List;

/**
 * Интерфейс сервиса для работы с задачами.
 */
public interface TaskService {

    /**
     * Создает новую задачу.
     *
     * @param taskDto ДТО с данными новой задачи.
     * @return ДТО созданной задачи с обновленными данными, включая ID.
     */
    TaskDto createTask(TaskDto taskDto);

    /**
     * Обновляет задачу по-заданному ID.
     *
     * @param id ID задачи, которую нужно обновить.
     * @param updatedTaskDto ДТО с обновленными данными задачи.
     * @return Обновленное ДТО задачи.
     * @throws TaskNotFoundException если задача с указанным ID не найдена.
     */
    TaskDto updateTask(Long id, TaskDto updatedTaskDto);

    /**
     * Удаляет задачу по-заданному ID.
     *
     * @param id ID задачи, которую нужно удалить.
     * @throws TaskNotFoundException если задача с указанным ID не найдена.
     */
    void deleteTask(Long id);

    /**
     * Получает задачу по-заданному ID.
     *
     * @param id ID задачи.
     * @return ДТО задачи с указанным ID.
     * @throws TaskNotFoundException если задача с указанным ID не найдена.
     */
    TaskDto getTaskById(Long id);

    /**
     * Получает список всех задач.
     *
     * @return Список ДТО всех задач.
     */
    List<TaskDto> getAllTasks();

    void sendStatusChangeNotification(String message);
}

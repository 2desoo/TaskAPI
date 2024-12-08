package com.dubrovsky.task.restful.controller;

import com.dubrovsky.task.restful.dto.TaskDTO;
import com.dubrovsky.task.restful.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления задачами в приложении.
 * Предоставляет CRUD операции для работы с задачами.
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    /**
     * Создает новую задачу.
     * @param taskDto объект, содержащий данные для создания задачи
     * @return созданная TaskDTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO createTask(@RequestBody TaskDTO taskDto) {
        return service.createTask(taskDto);
    }

    /**
     * Получает задачу по её ID.
     * @param id ID задачи, которую нужно получить
     * @return TaskDTO для запрашиваемого ID
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO getTaskById(@PathVariable Long id) {
        return service.getTaskById(id);
    }

    /**
     * Обновляет задачу по её ID.
     * @param id ID задачи для обновления
     * @param taskDto обновленные данные задачи
     * @return обновленная TaskDTO
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDto) {
        return service.updateTask(id, taskDto);
    }

    /**
     * Удаляет задачу по её ID.
     * @param id ID задачи для удаления
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        service.deleteTask(id);
    }

    /**
     * Получает все задачи.
     * @return список всех TaskDTO
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDTO> getAllTasks() {
        return service.getAllTasks();
    }
}

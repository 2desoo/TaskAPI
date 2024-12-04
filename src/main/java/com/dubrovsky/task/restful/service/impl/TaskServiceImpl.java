package com.dubrovsky.task.restful.service.impl;

import com.dubrovsky.task.restful.aspect.LoggingAround;
import com.dubrovsky.task.restful.aspect.LoggingException;
import com.dubrovsky.task.restful.aspect.LoggingExecution;
import com.dubrovsky.task.restful.aspect.LoggingReturn;
import com.dubrovsky.task.restful.exception.TaskNotFoundException;
import com.dubrovsky.task.restful.model.Task;
import com.dubrovsky.task.restful.repository.TaskRepository;
import com.dubrovsky.task.restful.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    public TaskServiceImpl(TaskRepository repository) {
        this.repository = repository;
    }

    @LoggingExecution
    public Task createTask(Task task) {
        return repository.save(task);
    }

    @LoggingAround
    public Task getTaskById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
    }

    @LoggingExecution
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    @LoggingReturn
    @LoggingException
    public Task updateTask(Long id, Task updatedTask) {
        return repository.findById(id).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setUserId(updatedTask.getUserId());
            return repository.save(task);
        }).orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
    }

    @LoggingAround
    public void deleteTask(Long id) {
        Task task = repository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
        repository.delete(task);
    }

}

package com.dubrovsky.task.restful.service;

import com.dubrovsky.task.restful.model.Task;

import java.util.List;

public interface TaskService {

    Task createTask(Task task);

    Task updateTask(Long id, Task updatedTask);

    void deleteTask(Long id);

    Task getTaskById(Long id);

    List<Task> getAllTasks();
}

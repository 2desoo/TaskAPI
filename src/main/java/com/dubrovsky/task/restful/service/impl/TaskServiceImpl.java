package com.dubrovsky.task.restful.service.impl;

import com.dubrovsky.task.restful.aspect.LoggingAround;
import com.dubrovsky.task.restful.aspect.LoggingException;
import com.dubrovsky.task.restful.aspect.LoggingExecution;
import com.dubrovsky.task.restful.aspect.LoggingReturn;
import com.dubrovsky.task.restful.dto.TaskDTO;
import com.dubrovsky.task.restful.exception.TaskNotFoundException;
import com.dubrovsky.task.restful.mapper.TaskMapper;
import com.dubrovsky.task.restful.model.Task;
import com.dubrovsky.task.restful.repository.TaskRepository;
import com.dubrovsky.task.restful.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;

    public TaskServiceImpl(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @LoggingExecution
    public TaskDTO createTask(TaskDTO taskDto) {
        Task task = mapper.toEntity(taskDto);
        Task savedTask = repository.save(task);
        return mapper.toDTO(savedTask);
    }

    @LoggingAround
    public TaskDTO getTaskById(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
        return mapper.toDTO(task);
    }

    @LoggingExecution
    public List<TaskDTO> getAllTasks() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @LoggingReturn
    @LoggingException
    public TaskDTO updateTask(Long id, TaskDTO updatedTaskDto) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
        task.setTitle(updatedTaskDto.getTitle());
        task.setDescription(updatedTaskDto.getDescription());
        task.setUserId(updatedTaskDto.getUserId());
        Task updatedTask = repository.save(task);
        return mapper.toDTO(updatedTask);
    }

    @LoggingAround
    public void deleteTask(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
        repository.delete(task);
    }
}

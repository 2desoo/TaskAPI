package com.dubrovsky.task.restful.service.impl;

import com.dubrovsky.task.restful.dto.TaskDto;
import com.dubrovsky.task.restful.exception.TaskNotFoundException;
import com.dubrovsky.task.restful.mapper.TaskMapper;
import com.dubrovsky.task.restful.model.Task;
import com.dubrovsky.task.restful.model.TaskStatus;
import com.dubrovsky.task.restful.repository.TaskRepository;
import com.dubrovsky.task.restful.service.TaskService;
import org.spring.dubrovsky.starter.LoggingAround;
import org.spring.dubrovsky.starter.LoggingException;
import org.spring.dubrovsky.starter.LoggingExecution;
import org.spring.dubrovsky.starter.LoggingReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final KafkaTemplate<String, String> clientKafkaTemplate;

    public void sendStatusChangeNotification(String message) {
        clientKafkaTemplate.send("task-status-topic", message);
    }

    @Autowired
    public TaskServiceImpl(KafkaTemplate<String, String> clientKafkaTemplate, TaskMapper mapper, TaskRepository repository) {
        this.clientKafkaTemplate = clientKafkaTemplate;
        this.mapper = mapper;
        this.repository = repository;
    }

    @LoggingExecution
    public TaskDto createTask(TaskDto taskDto) {
        Task task = mapper.toEntity(taskDto);
        Task savedTask = repository.save(task);
        return mapper.toDTO(savedTask);
    }

    @LoggingAround
    public TaskDto getTaskById(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
        return mapper.toDTO(task);
    }

    @LoggingExecution
    public List<TaskDto> getAllTasks() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @LoggingReturn
    @LoggingException
    public TaskDto updateTask(Long id, TaskDto updatedTaskDto) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));

        TaskStatus oldStatus = task.getStatus();

        task.setTitle(updatedTaskDto.getTitle());
        task.setDescription(updatedTaskDto.getDescription());
        task.setUserId(updatedTaskDto.getUserId());
        task.setStatus(updatedTaskDto.getStatus());

        Task updatedTask = repository.save(task);

        if (oldStatus != task.getStatus()) {
            sendStatusChangeNotification(String.valueOf(task));
        }

        TaskDto result = mapper.toDTO(updatedTask);

        if (result == null) {
            System.out.println("Error: result is null after mapping Task to TaskDto");
        } else {
            System.out.println("Successfully mapped Task to TaskDto: " + result);
        }

        return result;
    }

    @LoggingAround
    public void deleteTask(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
        repository.delete(task);
    }
}

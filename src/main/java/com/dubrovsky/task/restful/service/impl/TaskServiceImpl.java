package com.dubrovsky.task.restful.service.impl;

import com.dubrovsky.task.restful.aspect.LoggingAround;
import com.dubrovsky.task.restful.aspect.LoggingException;
import com.dubrovsky.task.restful.aspect.LoggingExecution;
import com.dubrovsky.task.restful.aspect.LoggingReturn;
import com.dubrovsky.task.restful.dto.TaskDto;
import com.dubrovsky.task.restful.exception.TaskNotFoundException;
import com.dubrovsky.task.restful.mapper.TaskMapper;
import com.dubrovsky.task.restful.model.Task;
import com.dubrovsky.task.restful.model.TaskStatus;
import com.dubrovsky.task.restful.repository.TaskRepository;
import com.dubrovsky.task.restful.service.TaskService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;

    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendStatusChangeNotification(String message) {
        kafkaTemplate.send("task-status-topic", message);
    }


    public TaskServiceImpl(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
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

        return mapper.toDTO(updatedTask);
    }

    @LoggingAround
    public void deleteTask(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
        repository.delete(task);
    }
}

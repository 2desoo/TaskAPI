package com.dubrovsky.task.restful.service.impl;

import com.dubrovsky.task.restful.dto.TaskDto;
import com.dubrovsky.task.restful.exception.TaskNotFoundException;
import com.dubrovsky.task.restful.mapper.TaskMapper;
import com.dubrovsky.task.restful.model.Task;
import com.dubrovsky.task.restful.model.TaskStatus;
import com.dubrovsky.task.restful.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private TaskMapper mapper;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private TaskServiceImpl service;

    @Test
    void testCreateTask() {
        TaskDto taskDto = new TaskDto();
        Task task = new Task();
        Task savedTask = new Task();
        TaskDto savedTaskDto = new TaskDto();

        when(mapper.toEntity(taskDto)).thenReturn(task);
        when(repository.save(task)).thenReturn(savedTask);
        when(mapper.toDTO(savedTask)).thenReturn(savedTaskDto);

        TaskDto result = service.createTask(taskDto);

        assertEquals(savedTaskDto, result);
        verify(repository).save(task);
        verify(mapper).toEntity(taskDto);
        verify(mapper).toDTO(savedTask);
    }

    @Test
    public void testUpdateTaskStatusChanged() {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.NEW);

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setStatus(TaskStatus.COMPLETED);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(task));
        Mockito.when(repository.save(any(Task.class))).thenReturn(updatedTask);

        TaskDto mockTaskDto = new TaskDto();
        mockTaskDto.setStatus(TaskStatus.COMPLETED);
        Mockito.when(mapper.toDTO(any(Task.class))).thenReturn(mockTaskDto);

        TaskDto taskDto = new TaskDto();
        taskDto.setStatus(TaskStatus.COMPLETED);

        TaskDto result = service.updateTask(1L, taskDto);

        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, result.getStatus());

        Mockito.verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    public void testGetTaskById_TaskNotFound() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> service.getTaskById(1L), "Expected TaskNotFoundException to be thrown");
    }

    @Test
    public void testGetTaskById_Success() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(TaskStatus.NEW);

        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Test Task");
        taskDto.setDescription("Test Description");
        taskDto.setStatus(TaskStatus.NEW);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(task));
        Mockito.when(mapper.toDTO(task)).thenReturn(taskDto);

        TaskDto result = service.getTaskById(1L);

        assertNotNull(result, "Expected TaskDto to be not null");
        assertEquals(1L, result.getId(), "Expected Task ID to be 1");
        assertEquals("Test Task", result.getTitle(), "Expected Task title to be 'Test Task'");
        assertEquals(TaskStatus.NEW, result.getStatus(), "Expected Task status to be NEW");
    }

    @Test
    public void testGetAllTasks_Success() {
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setStatus(TaskStatus.NEW);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(TaskStatus.COMPLETED);

        TaskDto taskDto1 = new TaskDto();
        taskDto1.setId(1L);
        taskDto1.setTitle("Task 1");
        taskDto1.setDescription("Description 1");
        taskDto1.setStatus(TaskStatus.NEW);

        TaskDto taskDto2 = new TaskDto();
        taskDto2.setId(2L);
        taskDto2.setTitle("Task 2");
        taskDto2.setDescription("Description 2");
        taskDto2.setStatus(TaskStatus.COMPLETED);

        List<Task> tasks = Arrays.asList(task1, task2);
        List<TaskDto> taskDtos = Arrays.asList(taskDto1, taskDto2);
        Mockito.when(repository.findAll()).thenReturn(tasks);
        Mockito.when(mapper.toDTO(task1)).thenReturn(taskDto1);
        Mockito.when(mapper.toDTO(task2)).thenReturn(taskDto2);

        List<TaskDto> result = service.getAllTasks();

        assertNotNull(result, "Expected list of TaskDto to be not null");
        assertEquals(2, result.size(), "Expected 2 TaskDto in the list");
        assertEquals("Task 1", result.get(0).getTitle(), "Expected Task title to be 'Task 1'");
        assertEquals("Task 2", result.get(1).getTitle(), "Expected Task title to be 'Task 2'");
    }

    @Test
    public void testGetAllTasks_EmptyList() {
        Mockito.when(repository.findAll()).thenReturn(Collections.emptyList());

        List<TaskDto> result = service.getAllTasks();

        assertNotNull(result, "Expected list of TaskDto to be not null");
        assertTrue(result.isEmpty(), "Expected empty list");
    }

    @Test
    public void testDeleteTask_Success() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task to delete");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(task));

        service.deleteTask(1L);

        Mockito.verify(repository, Mockito.times(1)).delete(task);
    }

    @Test
    public void testDeleteTask_TaskNotFound() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> service.deleteTask(1L), "Expected TaskNotFoundException to be thrown");
    }
}

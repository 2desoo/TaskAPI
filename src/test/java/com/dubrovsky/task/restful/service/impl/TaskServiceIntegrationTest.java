package com.dubrovsky.task.restful.service.impl;

import com.dubrovsky.task.restful.dto.TaskDto;
import com.dubrovsky.task.restful.model.Task;
import com.dubrovsky.task.restful.model.TaskStatus;
import com.dubrovsky.task.restful.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TaskServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");

    @Autowired
    private TaskRepository repository;

    @Test
    void testCreateTaskIntegration() {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Test Task");

        Task savedTask = repository.save(new Task(
                null,                // id (null, если оно генерируется автоматически)
                taskDto.getTitle(),  // title
                null,                // description
                null,                // userId
                TaskStatus.NEW       // статус, если нужно указать значение
        ));
        assertNotNull(savedTask.getId());
    }
}


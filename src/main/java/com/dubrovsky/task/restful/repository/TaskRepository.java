package com.dubrovsky.task.restful.repository;

import com.dubrovsky.task.restful.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с задачами
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
}

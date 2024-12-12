package com.dubrovsky.task.restful.dto;

import java.util.Objects;

public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TaskDto taskDTO = (TaskDto) o;
        return Objects.equals(id, taskDTO.id) && Objects.equals(title, taskDTO.title) && Objects.equals(description, taskDTO.description) && Objects.equals(userId, taskDTO.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, userId);
    }

    @Override
    public String toString() {
        return "TaskDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", userId=" + userId +
                '}';
    }
}

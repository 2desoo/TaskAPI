package com.dubrovsky.task.restful.mapper;

import com.dubrovsky.task.restful.dto.TaskDto;
import com.dubrovsky.task.restful.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Маппер для преобразования объектов Task в TaskDTO и наоборот.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    /**
     * Преобразует объект Task в TaskDTO.
     *
     * @param task Объект задачи.
     * @return Объект TaskDTO.
     */
    TaskDto toDTO(Task task);

    /**
     * Преобразует объект TaskDTO в Task.
     *
     * @param taskDTO Объект TaskDTO.
     * @return Объект Task.
     */
    Task toEntity(TaskDto taskDTO);
}

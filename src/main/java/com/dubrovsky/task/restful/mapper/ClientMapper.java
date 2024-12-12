package com.dubrovsky.task.restful.mapper;

import com.dubrovsky.task.restful.dto.ClientDto;
import com.dubrovsky.task.restful.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {

    ClientDto toDto(Client client);

    Client toEntity(ClientDto clientDto);
}

package com.dubrovsky.task.restful.service;

import com.dubrovsky.task.restful.dto.ClientDto;
import com.dubrovsky.task.restful.model.Client;

import java.util.List;

public interface ClientService {

    List<ClientDto> parseJson();

    void registerClient(List<Client> clients);
}

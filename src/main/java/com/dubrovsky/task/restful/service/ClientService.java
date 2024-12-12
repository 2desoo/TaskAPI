package com.dubrovsky.task.restful.service;

import com.dubrovsky.task.restful.dto.ClientDto;
import com.dubrovsky.task.restful.model.Client;

import java.util.List;

public interface ClientService {

    ClientDto createClient(ClientDto clientDto);

    ClientDto getClientById(Long id);

    ClientDto updateClient(Long id, ClientDto clientDto);

    void deleteClient(Long id);

    List<ClientDto> getAllClients();

    List<ClientDto> parseJson();

    void registerClient(List<Client> clients);
}

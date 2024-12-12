package com.dubrovsky.task.restful.service.impl;

import com.dubrovsky.task.restful.dto.ClientDto;
import com.dubrovsky.task.restful.exception.ClientNotFoundException;
import com.dubrovsky.task.restful.kafka.KafkaClientProducer;
import com.dubrovsky.task.restful.mapper.ClientMapper;
import com.dubrovsky.task.restful.model.Client;
import com.dubrovsky.task.restful.repository.ClientRepository;
import com.dubrovsky.task.restful.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;
    private final KafkaClientProducer kafkaClientProducer;

    public ClientServiceImpl(ClientRepository repository, ClientMapper mapper, KafkaClientProducer kafkaClientProducer) {
        this.repository = repository;
        this.mapper = mapper;
        this.kafkaClientProducer = kafkaClientProducer;
    }

    public ClientDto createClient(ClientDto clientDto) {
        Client client = mapper.toEntity(clientDto);
        Client savedClient = repository.save(client);
        return mapper.toDto(savedClient);
    }

    public ClientDto getClientById(Long id) {
        Client client = repository.findById(id).orElseThrow(() -> new ClientNotFoundException("Client with ID " + id + " not found"));
        return mapper.toDto(client);
    }

    public ClientDto updateClient(Long id, ClientDto clientDto) {
        Client client = repository.findById(id).orElseThrow(() -> new ClientNotFoundException("Client with ID " + id + " not found"));
        client.setName(clientDto.getName());
        client.setEmail(clientDto.getEmail());
        Client updatedClient = repository.save(client);
        return mapper.toDto(updatedClient);
    }

    public void deleteClient(Long id) {
        Client client = repository.findById(id).orElseThrow(() -> new ClientNotFoundException("Client with ID " + id + " not found"));
        repository.delete(client);
    }

    public List<ClientDto> getAllClients() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ClientDto> parseJson() {
        List<ClientDto> clients = new ArrayList<>();

        try (InputStream inputStream = new FileInputStream("src/main/resources/MOCK_DATA.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            ClientDto[] clientDtos = objectMapper.readValue(inputStream, ClientDto[].class);

            clients.addAll(Arrays.asList(clientDtos));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clients;
    }

    public void registerClient(List<Client> clients) {
        repository.saveAll(clients)
                .stream()
                .map(Client::getId)
                .forEach(kafkaClientProducer::send);
    }
}

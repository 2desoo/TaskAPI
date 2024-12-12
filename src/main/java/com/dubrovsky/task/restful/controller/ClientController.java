package com.dubrovsky.task.restful.controller;

import com.dubrovsky.task.restful.dto.ClientDto;
import com.dubrovsky.task.restful.kafka.KafkaClientProducer;
import com.dubrovsky.task.restful.service.ClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;
    private final KafkaClientProducer kafkaClientProducer;
    @Value("${kafka.topic}")
    private String topic;

    public ClientController(ClientService clientService, KafkaClientProducer kafkaClientProducer) {
        this.clientService = clientService;
        this.kafkaClientProducer = kafkaClientProducer;
    }

    @GetMapping("/parse")
    @ResponseStatus(HttpStatus.OK)
    public void parse() {
        List<ClientDto> clients = clientService.parseJson();
        clients.forEach(client -> kafkaClientProducer.sendTo(topic, client));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDto createClient(ClientDto clientDto) {
        return clientService.createClient(clientDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientDto getClientById(@PathVariable Long id) {
        return clientService.getClientById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientDto updateClient(@PathVariable Long id, @RequestBody ClientDto clientDto) {
        return clientService.updateClient(id, clientDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClientDto> getAllClients() {
        return clientService.getAllClients();
    }
}

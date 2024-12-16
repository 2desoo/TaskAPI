package com.dubrovsky.task.restful.controller;

import com.dubrovsky.task.restful.config.KafkaConfig;
import com.dubrovsky.task.restful.dto.ClientDto;
import com.dubrovsky.task.restful.kafka.KafkaClientProducer;
import com.dubrovsky.task.restful.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;
    private final KafkaClientProducer kafkaClientProducer;
    private final KafkaConfig config;

    public ClientController(ClientService clientService, KafkaClientProducer kafkaClientProducer, KafkaConfig config) {
        this.clientService = clientService;
        this.kafkaClientProducer = kafkaClientProducer;
        this.config = config;
    }

    @GetMapping("/parse")
    @ResponseStatus(HttpStatus.OK)
    public void parse() {
        List<ClientDto> clients = clientService.parseJson();
        clients.forEach(client -> kafkaClientProducer.sendTo(config.topic().name(), client));
    }
}

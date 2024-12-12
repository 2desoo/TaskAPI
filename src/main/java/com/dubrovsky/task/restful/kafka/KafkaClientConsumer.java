package com.dubrovsky.task.restful.kafka;

import com.dubrovsky.task.restful.dto.ClientDto;
import com.dubrovsky.task.restful.mapper.ClientMapper;
import com.dubrovsky.task.restful.model.Client;
import com.dubrovsky.task.restful.service.ClientService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaClientConsumer {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    public KafkaClientConsumer(ClientService clientService, ClientMapper clientMapper) {
        this.clientService = clientService;
        this.clientMapper = clientMapper;
    }

    @KafkaListener(id = "${kafka.group-id}", topics = "${kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<ClientDto> messages,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {
            List<Client> clients = messages.stream()
                    .map(clientDto -> {
                        clientDto.setName(key + "@" + clientDto.getName());
                        return clientMapper.toEntity(clientDto);
                    })
                    .toList();
            clientService.registerClient(clients);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

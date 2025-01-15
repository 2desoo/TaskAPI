package com.dubrovsky.task.restful.service.impl;

import com.dubrovsky.task.restful.dto.ClientDto;
import com.dubrovsky.task.restful.kafka.KafkaClientProducer;
import com.dubrovsky.task.restful.mapper.ClientMapper;
import com.dubrovsky.task.restful.model.Client;
import com.dubrovsky.task.restful.repository.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository repository;

    @Mock
    private ClientMapper mapper;

    @InjectMocks
    private ClientServiceImpl service;

    @Mock
    private KafkaClientProducer kafkaClientProducer;

    @Test
    void testParseJson() throws IOException {
        InputStream inputStream = mock(InputStream.class);
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        ClientDto[] clientDtos = { new ClientDto() };

        when(objectMapper.readValue(inputStream, ClientDto[].class)).thenReturn(clientDtos);

        ClientServiceImpl service = new ClientServiceImpl(repository, mapper, kafkaClientProducer);
        List<ClientDto> result = service.parseJson();

        assertFalse(result.isEmpty());
    }

    @Test
    public void testRegisterClient_Success() {
        Client client1 = new Client();
        client1.setId(1L);
        client1.setName("Client 1");

        Client client2 = new Client();
        client2.setId(2L);
        client2.setName("Client 2");

        List<Client> clients = Arrays.asList(client1, client2);

        Mockito.when(repository.saveAll(clients)).thenReturn(clients);  // Этот объект repository является мокированным

        Mockito.doNothing().when(kafkaClientProducer).send(Mockito.anyLong());  // Этот объект kafkaClientProducer тоже является мокированным

        service.registerClient(clients);

        Mockito.verify(repository, Mockito.times(1)).saveAll(clients);

        Mockito.verify(kafkaClientProducer, Mockito.times(2)).send(Mockito.anyLong());
    }
}
package com.dubrovsky.task.restful.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KafkaClientProducer {

    private final KafkaTemplate kafkaTemplate;

    public KafkaClientProducer(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Long id) {
        try {
            kafkaTemplate.sendDefault(UUID.randomUUID().toString(), id).get();
            kafkaTemplate.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTo(String topic, Object o) {
        try {
            kafkaTemplate.send(topic, o).get();
            kafkaTemplate.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

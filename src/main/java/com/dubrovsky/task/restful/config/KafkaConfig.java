package com.dubrovsky.task.restful.config;

import com.dubrovsky.task.restful.dto.ClientDto;
import com.dubrovsky.task.restful.kafka.KafkaClientProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${kafka.bootstrap-servers}")
    private String servers;

    @Value("${kafka.group-id}")
    private String groupId;

    @Value("${kafka.topic}")
    private String topic;

    @Bean
    public ConsumerFactory<String, ClientDto> consumerListenerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ClientDto.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        DefaultKafkaConsumerFactory factory = new DefaultKafkaConsumerFactory<String, ClientDto>(props);
        factory.setKeyDeserializer(new StringDeserializer());

        return factory;
    }

    @Bean("client")
    public KafkaTemplate<String, ClientDto> kafkaTemplate(ProducerFactory<String, ClientDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, ClientDto> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    @ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = true)
    public KafkaClientProducer kafkaClientProducer(@Qualifier("client") KafkaTemplate kafkaTemplate) {
        kafkaTemplate.setDefaultTopic(topic);
        return new KafkaClientProducer(kafkaTemplate);
    }

    private <T> void factoryBuilder(ConsumerFactory<String, T> consumerFactory, ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(1);
        factory.getContainerProperties().setPollTimeout(3000);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setMicrometerEnabled(true);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClientDto> kafkaListenerContainerFactory(@Qualifier("consumerListenerFactory") ConsumerFactory<String, ClientDto> consumerListenerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, ClientDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(consumerListenerFactory, factory);
        return factory;
    }
}


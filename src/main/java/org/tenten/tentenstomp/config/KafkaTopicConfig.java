package org.tenten.tentenstomp.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.tenten.tentenstomp.global.common.constant.TopicConstant.*;

@Configuration
public class KafkaTopicConfig {
    @Value("${kafka.producer}")
    private String host;

    @Bean
    public NewTopic newTopic() {
        return new NewTopic("kafka", 1, (short) 1);
    }

    @Bean
    public NewTopic tripInfo() {
        return new NewTopic(TRIP_INFO, 1, (short) 1);
    }

    @Bean
    public NewTopic tripItem() {
        return new NewTopic(TRIP_ITEM, 1, (short) 1);
    }

    @Bean
    public NewTopic path() {
        return new NewTopic(PATH, 1, (short) 1);
    }

    @Bean
    public NewTopic connectedMember() {
        return new NewTopic(MEMBER, 1, (short) 1);
    }

    @Bean
    public NewTopic cursor() {
        return new NewTopic(CURSOR, 1, (short) 1);
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> adminConfigMap = new HashMap<>();
        adminConfigMap.put(BOOTSTRAP_SERVERS_CONFIG, host);
        return new KafkaAdmin(adminConfigMap);
    }
}

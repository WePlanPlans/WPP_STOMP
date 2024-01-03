package org.tenten.tentenstomp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry endpointRegistry) {
        endpointRegistry.addEndpoint("/ws-stomp") // 소켓 연결 Endpoint 설정
            .setAllowedOrigins("*").withSockJS(); // Todo 추후 특정 url 변경

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry brokerRegistry) {
        // 클라이언트가 Server로 메세지 발행 -> @MessageMapping 이 붙어있는 메서드와 연결
        brokerRegistry.setApplicationDestinationPrefixes("/pub");
        // 메서드에서 처리된 메세지의 결과를 broker를 통해서 /sub/message 를 구독하고 있는
        // 모든 클라이언트들에게 메세지를 전달
        brokerRegistry.enableSimpleBroker("/sub");
    }
}

package org.tenten.tentenstomp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.tenten.tentenstomp.global.stomp.StompExceptionHandler;
import org.tenten.tentenstomp.global.stomp.StompPreHandler;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompExceptionHandler stompExceptionHandler;
    private final StompPreHandler stompPreHandler;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry endpointRegistry) {
        endpointRegistry.addEndpoint("/ws-stomp")// 소켓 연결 Endpoint 설정
            .setAllowedOriginPatterns("http://*:8080", "http://*.*.*.*:8080", "https://jxy.me/", "http://localhost:5173", "https://weplanplans.vercel.app", "https://dev-weplanplans.vercel.app");
        endpointRegistry.setErrorHandler(stompExceptionHandler);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry brokerRegistry) {
        brokerRegistry.setApplicationDestinationPrefixes("/pub");
        brokerRegistry.enableSimpleBroker("/sub").setHeartbeatValue(new long[]{1000, 1000}).setTaskScheduler(taskScheduler());

    }

    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();
        return taskScheduler;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompPreHandler);
    }
}

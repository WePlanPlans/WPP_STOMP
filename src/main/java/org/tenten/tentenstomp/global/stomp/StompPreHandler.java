package org.tenten.tentenstomp.global.stomp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StompPreHandler implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        log.info("msgType : " + headerAccessor.getMessageType().toString() + " dest : " + headerAccessor.getDestination());
//        String authorizationHeader = String.valueOf(headerAccessor.getNativeHeader("Authorization"));
//        if (authorizationHeader == null || authorizationHeader.equals("null")) {
//            throw new MessageDeliveryException("UNAUTHORIZED");
//        }
        return ChannelInterceptor.super.preSend(message, channel);
    }
}

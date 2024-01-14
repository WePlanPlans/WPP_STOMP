package org.tenten.tentenstomp.global.stomp;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.messaging.simp.stomp.StompCommand.ERROR;

@Component
public class StompExceptionHandler extends StompSubProtocolErrorHandler {
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        if (ex.getMessage().equals("UNAUTHORIZED")) {
            return errorMessage("유효하지 않은 권한");
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }


    private Message<byte[]> errorMessage(String errorMessage) {

        StompHeaderAccessor accessor = StompHeaderAccessor.create(ERROR);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(errorMessage.getBytes(UTF_8),
            accessor.getMessageHeaders());
    }
}

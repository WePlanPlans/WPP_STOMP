package org.tenten.tentenstomp.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.tenten.tentenstomp.global.messaging.redis.subscriber.RedisSubscriber;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RedisChannelUtil {
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscriber redisSubscriber;
    private final Map<String, ChannelTopic> channelTopicMap = new HashMap<>();

    public ChannelTopic getChannelTopic(String tripId, String endPoint) {
        String channelName = createChannelName(tripId, endPoint);
        if (!channelTopicMap.containsKey(channelName)) {
            ChannelTopic newTopic = new ChannelTopic(channelName);
            channelTopicMap.put(channelName, newTopic);
            redisMessageListenerContainer.addMessageListener(redisSubscriber, newTopic);
        }
        return channelTopicMap.get(channelName);
    }

    public ChannelTopic getChannelTopic(String tripId, String visitDate, String endPoint) {
        String channelName = createChannelName(tripId, visitDate, endPoint);
        if (!channelTopicMap.containsKey(channelName)) {
            ChannelTopic newTopic = new ChannelTopic(channelName);
            channelTopicMap.put(channelName, newTopic);
            redisMessageListenerContainer.addMessageListener(redisSubscriber, newTopic);
        }
        return channelTopicMap.get(channelName);
    }

    private String createChannelName(String tripId, String endPoint) {
        return "/" + tripId + "/" + endPoint;
    }

    private String createChannelName(String tripId, String visitDate, String endPoint) {
        return "/" + tripId + "/" + endPoint + "/" + visitDate;
    }
}

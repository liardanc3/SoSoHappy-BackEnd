package sosohappy.feedservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sosohappy.feedservice.service.FeedService;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ConcurrentHashMap<String, String> emailAndTokenMap;
    private final FeedService feedService;

    @KafkaListener(topics = "accessToken", groupId = "feed-service-accessToken-0000")
    public void addAccessToken(ConsumerRecord<byte[], byte[]> record){
        String email = new String(record.key());
        String accessToken = new String(record.value());

        System.out.println("email = " + email);
        System.out.println("accessToken = " + accessToken);
        emailAndTokenMap.put(email, accessToken);
    }

    @KafkaListener(topics = "expired", groupId = "feed-service-expired-0000")
    public void handleExpiredToken(ConsumerRecord<byte[], byte[]> record){

        String email = new String(record.key());

        emailAndTokenMap.remove(email);
    }

    @KafkaListener(topics = "resign", groupId = "feed-service-resign-0000")
    public void handleResignedUser(ConsumerRecord<byte[], byte[]> record){

        String email = new String(record.key());
        String nickname = new String(record.value());

        emailAndTokenMap.remove(email);
        feedService.deleteDataOfResignedUser(nickname);
    }

}

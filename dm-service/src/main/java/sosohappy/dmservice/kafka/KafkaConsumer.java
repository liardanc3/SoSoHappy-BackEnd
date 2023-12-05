package sosohappy.dmservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    public static final ConcurrentHashMap<String, String> emailAndTokenMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "accessToken", groupId = "dm-service-accessToken-0000")
    public void addAccessToken(ConsumerRecord<byte[], byte[]> record){
        String email = new String(record.key());
        String accessToken = new String(record.value());

        emailAndTokenMap.put(email, accessToken);
    }

    @KafkaListener(topics = "expired", groupId = "dm-service-expired-0000")
    public void handleExpiredToken(ConsumerRecord<byte[], byte[]> record){
        String email = new String(record.key());

        emailAndTokenMap.remove(email);
    }

    @KafkaListener(topics = "resign", groupId = "dm-service-resign-0000")
    public void handleResignedUser(ConsumerRecord<byte[], byte[]> record){
        String email = new String(record.key());

        emailAndTokenMap.remove(email);
    }

}


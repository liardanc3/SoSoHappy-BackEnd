package sosohappy.feedservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class KafkaConsumer {

    public static ConcurrentHashMap<String, String> emailAndTokenMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "accessToken", groupId = "dasfadf")
    public void addAccessToken(ConsumerRecord<String, String> record){
        String email = record.key();
        String accessToken = record.value();

        emailAndTokenMap.put(email, accessToken);
    }

}

package sosohappy.dmservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ConcurrentHashMap<String, String> emailAndTokenMap;

    @KafkaListener(topics = "accessToken", groupId = "ASddd2ddsafdaddjdasdd")
    public void addAccessToken(ConsumerRecord<String, String> record){
        String email = String.valueOf(record.key());
        String accessToken = record.value();

        emailAndTokenMap.put(email, accessToken);
    }

}


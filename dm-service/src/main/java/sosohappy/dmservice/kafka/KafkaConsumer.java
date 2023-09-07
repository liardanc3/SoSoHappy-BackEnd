package sosohappy.dmservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ConcurrentHashMap<String, String> emailAndTokenMap;

    @KafkaListener(topics = "accessToken", groupId = "ASddd2ddsafddaddjddacssddsdd")
    public void addAccessToken(ConsumerRecord<byte[], byte[]> record){

        String email = new String(record.key());
        String accessToken = new String(record.value());

        System.out.println("email = " + email);
        System.out.println("accessToken = " + accessToken);
        emailAndTokenMap.put(email, accessToken);
    }

}


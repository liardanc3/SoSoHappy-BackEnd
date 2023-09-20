package sosohappy.dmservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sosohappy.dmservice.service.MessageService;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ConcurrentHashMap<String, String> emailAndTokenMap;
    private final MessageService messageService;

    @KafkaListener(topics = "accessToken", groupId = "ljkdadddlj")
    public void addAccessToken(ConsumerRecord<byte[], byte[]> record){

        String email = new String(record.key());
        String accessToken = new String(record.value());

        emailAndTokenMap.put(email, accessToken);
    }

    @KafkaListener(topics = "resign", groupId = "ljkdadddlj")
    public void handleResignedUser(ConsumerRecord<byte[], byte[]> record){

        String email = new String(record.key());
        String nickname = new String(record.value());

        emailAndTokenMap.remove(email);
        messageService.closeSession(nickname);
    }

}


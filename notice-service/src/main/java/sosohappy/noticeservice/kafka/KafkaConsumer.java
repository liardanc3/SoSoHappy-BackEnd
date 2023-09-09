package sosohappy.noticeservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sosohappy.noticeservice.service.NoticeService;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final NoticeService noticeService;

    private static final ConcurrentHashMap<String, String> emailAndTokenMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "accessToken", groupId = "ASddd2ddsafdadddasdd")
    public void addAccessToken(ConsumerRecord<byte[], byte[]> record){
        String email = new String(record.key());
        String accessToken = new String(record.value());

        emailAndTokenMap.put(email, accessToken);
    }



}

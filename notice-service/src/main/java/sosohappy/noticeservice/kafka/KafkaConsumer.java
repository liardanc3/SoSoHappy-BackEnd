package sosohappy.noticeservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sosohappy.noticeservice.service.NoticeService;
import sosohappy.noticeservice.util.Utils;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    public static ConcurrentHashMap<String, String> emailAndAccessTokenMap;
    public static ConcurrentHashMap<String, String> emailAndDeviceTokenMap;

    private final NoticeService noticeService;
    private final Utils utils;

    @KafkaListener(topics = "accessToken", groupId = "notice-service-accessToken-0000")
    public void addAccessToken(ConsumerRecord<byte[], byte[]> record){
        String email = new String(record.key());
        String accessToken = new String(record.value());

        emailAndAccessTokenMap.put(email, accessToken);
    }

    @KafkaListener(topics = "expired", groupId = "notice-service-expired-0000")
    public void handleExpiredToken(ConsumerRecord<byte[], byte[]> record){

        String email = new String(record.key());

        emailAndAccessTokenMap.remove(email);
    }

    @KafkaListener(topics = "noticeLike", groupId = "notice-service-noticeLike-0000")
    public void noticeLike(ConsumerRecord<byte[], byte[]> record){
        String liker = new String(record.key());
        String[] nicknameAndDateStr = new String(record.value()).split(",");
        String nickname = nicknameAndDateStr[0];
        Long date = Long.parseLong(nicknameAndDateStr[1]);

        //
    }

    @KafkaListener(topics = "resign", groupId = "notice-service-resign-0000")
    public void handleResignedUser(ConsumerRecord<byte[], byte[]> record){

        String email = new String(record.key());
        String nickname = new String(record.value());

        emailAndAccessTokenMap.remove(email);
    }

    @KafkaListener(topics = "deviceToken", groupId = "notice-service-deviceToken-0000")
    public void handleDeviceToken(ConsumerRecord<byte[], byte[]> record){

        String email = new String(record.key());
        String deviceToken = new String(record.value());

        emailAndDeviceTokenMap.put(email, deviceToken);

    }

}

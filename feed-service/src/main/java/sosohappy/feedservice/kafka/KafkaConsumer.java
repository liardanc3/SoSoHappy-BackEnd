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

    public static final ConcurrentHashMap<String, String> emailAndTokenMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, String> emailAndNicknameMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, String> nicknameAndEmailMap = new ConcurrentHashMap<>();

    private final FeedService feedService;

    @KafkaListener(topics = "accessToken", groupId = "feed-service-accessToken-0000")
    public void addAccessToken(ConsumerRecord<byte[], byte[]> record){
        String email = new String(record.key());
        String accessToken = new String(record.value());

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

    @KafkaListener(topics = "emailAndNickname", groupId = "feed-service-emailAndNickname-0000")
    public void handleUpdateEmailAndNickname(ConsumerRecord<byte[], byte[]> record){
        String email = new String(record.key());
        String nickname = new String(record.value());

        String originNickname = emailAndNicknameMap.get(email);

        nicknameAndEmailMap.remove(originNickname);
        emailAndNicknameMap.put(email, nickname);
        nicknameAndEmailMap.put(nickname, email);

        feedService.updateNickname(originNickname, nickname);
    }
}

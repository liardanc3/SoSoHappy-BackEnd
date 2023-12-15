package sosohappy.noticeservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import sosohappy.noticeservice.service.NoticeService;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    public static ConcurrentHashMap<String, String> emailAndDeviceTokenMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> emailAndNicknameMap = new ConcurrentHashMap<>();

    private final NoticeService noticeService;

    @KafkaListener(topics = "noticeLike", groupId = "notice-service-noticeLike-0004")
    public Disposable noticeLike(ConsumerRecord<byte[], byte[]> record){
        return Mono.defer(() -> {
            String[] nicknameAndDateStr = new String(record.value()).split(",");

            String srcNickname = new String(record.key());
            String email = nicknameAndDateStr[0];
            String dstNickname = emailAndNicknameMap.get(email);

            Long date = Long.parseLong(nicknameAndDateStr[1]);

            return noticeService.sendNotice(srcNickname, dstNickname, date, emailAndDeviceTokenMap.get(email));
        }).subscribe();
    }

    @KafkaListener(topics = "emailAndNickname", groupId = "notice-service-emailAndNickname-0000")
    public Disposable handleEmailAndNickname(ConsumerRecord<byte[], byte[]> record){
        return Mono.fromRunnable(() -> {
            String email = new String(record.key());
            String nickname = new String(record.key());

            emailAndNicknameMap.put(email, nickname);
        }).subscribe();
    }

    @KafkaListener(topics = "deviceToken", groupId = "notice-service-deviceToken-0008")
    public Disposable handleDeviceToken(ConsumerRecord<byte[], byte[]> record){
        return Mono.fromRunnable(() -> emailAndDeviceTokenMap.put(new String(record.key()), new String(record.value())))
                .subscribe();
    }

    @KafkaListener(topics = "directMessage", groupId = "notice-service-directMessage-0000")
    public Disposable handleDirectMessage(ConsumerRecord<byte[], byte[]> record){
        return Mono.defer(() -> {
            String deviceToken = new String(record.key());
            String messageDtoStr = new String(record.value());

            return noticeService.sendDirectMessage(deviceToken, messageDtoStr);
        }).subscribe();
    }

    @KafkaListener(topics = "resign", groupId = "notice-service-resign-0000")
    public void handleResignedUser(ConsumerRecord<byte[], byte[]> record){
        String email = new String(record.key());

        emailAndDeviceTokenMap.remove(email);
    }


}

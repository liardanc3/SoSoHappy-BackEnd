package sosohappy.noticeservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import sosohappy.noticeservice.service.NoticeService;
import sosohappy.noticeservice.util.Utils;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    public static ConcurrentHashMap<String, String> emailAndDeviceTokenMap = new ConcurrentHashMap<>();

    private final NoticeService noticeService;

    @KafkaListener(topics = "noticeLike", groupId = "notice-service-noticeLike-0000")
    public Mono<Void> noticeLike(ConsumerRecord<byte[], byte[]> record){
        return Mono.fromRunnable(() -> {
            String[] nicknameAndDateStr = new String(record.value()).split(",");
            System.out.println("devicetoken = " + new String(record.value()));
            String srcNickname = new String(record.key());
            String email = nicknameAndDateStr[0];
            Long date = Long.parseLong(nicknameAndDateStr[1]);
            noticeService.sendNotice(srcNickname, date, emailAndDeviceTokenMap.get(email));
        });
    }

    @KafkaListener(topics = "deviceToken", groupId = "notice-service-deviceToken-0000")
    public Mono<Void> handleDeviceToken(ConsumerRecord<byte[], byte[]> record){
        System.out.println("devicetoken = " + new String(record.value()));
        return Mono.fromRunnable(() -> emailAndDeviceTokenMap.put(new String(record.key()), new String(record.value())));
    }

    @KafkaListener(topics = "directMessage", groupId = "notice-service-directMessage-0000")
    public Mono<Void> handleDirectMessage(ConsumerRecord<byte[], byte[]> record){
        return Mono.defer(() -> {
            String deviceToken = new String(record.key());
            String messageDtoStr = new String(record.value());

            return noticeService.sendDirectMessage(deviceToken, messageDtoStr);
        });
    }

    @KafkaListener(topics = "resign", groupId = "notice-service-resign-0000")
    public void handleResignedUser(ConsumerRecord<byte[], byte[]> record){
        String email = new String(record.key());

        emailAndDeviceTokenMap.remove(email);
    }


}

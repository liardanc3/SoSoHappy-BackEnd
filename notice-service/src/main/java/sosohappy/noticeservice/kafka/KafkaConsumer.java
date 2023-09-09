package sosohappy.noticeservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sosohappy.noticeservice.data.LikeNotice;
import sosohappy.noticeservice.data.Notice;
import sosohappy.noticeservice.service.NoticeService;
import sosohappy.noticeservice.util.Utils;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final NoticeService noticeService;
    private final Utils utils;
    private final ConcurrentHashMap<String, String> emailAndTokenMap;

    @KafkaListener(topics = "accessToken", groupId = "hjkjdd")
    public void addAccessToken(ConsumerRecord<byte[], byte[]> record){
        String email = new String(record.key());
        String accessToken = new String(record.value());

        emailAndTokenMap.put(email, accessToken);
    }

    @KafkaListener(topics = "notice-like", groupId = "Asdasddas")
    public void noticeLike(ConsumerRecord<byte[], byte[]> record){
        String liker = new String(record.key());
        String[] nicknameAndDateStr = new String(record.value()).split(",");
        String nickname = nicknameAndDateStr[0];
        Long date = Long.parseLong(nicknameAndDateStr[1]);

        noticeService.sendNotice(
                nickname,
                utils.objectToString(
                        Notice.builder()
                                .topic("like")
                                .data(
                                        LikeNotice.builder()
                                                .liker(liker)
                                                .date(date)
                                                .build()
                                )
                                .build()
                )
        );
    }




}

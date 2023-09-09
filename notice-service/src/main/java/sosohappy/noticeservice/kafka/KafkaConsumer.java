package sosohappy.noticeservice.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sosohappy.noticeservice.dto.LikeNoticeDto;
import sosohappy.noticeservice.service.NoticeService;
import sosohappy.noticeservice.util.Utils;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final NoticeService noticeService;
    private final Utils utils;

    public static ConcurrentHashMap<String, String> emailAndTokenMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "accessToken", groupId = "ASddd2ddsafdadddasdd")
    public void addAccessToken(ConsumerRecord<byte[], byte[]> record){
        String email = new String(record.key());
        String accessToken = new String(record.value());

        emailAndTokenMap.put(email, accessToken);
    }

    @KafkaListener(topics = "notice-like", groupId = "Asdasdas")
    public void noticeLike(ConsumerRecord<byte[], byte[]> record){
        String liker = new String(record.key());
        String[] nicknameAndDateStr = new String(record.value()).split(",");
        String nickname = nicknameAndDateStr[0];
        Double date = Double.parseDouble(nicknameAndDateStr[1]);

        noticeService.sendNotice(
                nickname,
                utils.objectToString(LikeNoticeDto.builder()
                        .liker(liker)
                        .date(date)
                        .build()
                )
        );
    }


}

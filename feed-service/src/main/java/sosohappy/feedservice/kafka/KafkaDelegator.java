package sosohappy.feedservice.kafka;

import org.springframework.stereotype.Component;
import sosohappy.feedservice.domain.dto.NicknameAndDateDto;

import java.util.List;

@Component
public class KafkaDelegator {

    @KafkaProducer(topic = "noticeLike")
    public List<String> produceUpdateLike(String srcNickname, NicknameAndDateDto nicknameAndDateDto) {
        return List.of(srcNickname, KafkaConsumer.nicknameAndEmailMap.get(nicknameAndDateDto.getNickname()) + "," + nicknameAndDateDto.getDate());
    }
}

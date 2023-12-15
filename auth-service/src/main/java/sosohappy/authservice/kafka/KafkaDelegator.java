package sosohappy.authservice.kafka;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaDelegator {

    @KafkaProducer(topic = "resign")
    public List<String> produceResign(String email, String nickname){
        return List.of(email, nickname);
    }

    @KafkaProducer(topic = "emailAndNickname")
    public List<String> produceEmailAndNickname(String email, String nickname){
        return List.of(email, nickname);
    }

    @KafkaProducer(topic = "deviceToken")
    public List<String> produceDeviceToken(String email, String deviceToken){
        return List.of(email, deviceToken);
    }

}

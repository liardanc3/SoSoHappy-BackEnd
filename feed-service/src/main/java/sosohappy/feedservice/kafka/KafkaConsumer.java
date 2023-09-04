package sosohappy.feedservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class KafkaConsumer {

    private static ConcurrentHashMap<String, String> emailToTokenMap;

    @KafkaListener(topics = "accessToken")
    public void addAccessToken(String email, String accessToken){
        System.out.println("email = " + email);
        System.out.println("accessToken = " + accessToken);

        emailToTokenMap.put(email, accessToken);

        for (String value : emailToTokenMap.values()) {
            System.out.println("value = " + value);
        }
    }

}

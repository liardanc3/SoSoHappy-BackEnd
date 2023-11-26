package sosohappy.feedservice.kafka;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class KafkaProducerAspect {

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;

    @AfterReturning(value = "@annotation(kafkaProducer)", returning = "result")
    public void handleKafkaProducer(KafkaProducer kafkaProducer, Object result){

        if(kafkaProducer.topic().equals("noticeLike")){
            List<String> likeResult = (List<String>) result;

            String srcNickname = likeResult.get(0);
            String emailAndDate = likeResult.get(1);

            kafkaTemplate.send(kafkaProducer.topic(), srcNickname.getBytes(), emailAndDate.getBytes());
        }

    }

}

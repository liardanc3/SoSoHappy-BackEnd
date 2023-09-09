package sosohappy.feedservice.kafka;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class KafkaProducerAspect {

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;

    @AfterReturning(value = "@annotation(kafkaProducer)", returning = "result")
    public void handleKafkaProducer(JoinPoint joinPoint, KafkaProducer kafkaProducer, Object result){

        // notice-like
        if(result instanceof List<?>){
            List<String> likeResult = (List<String>) result;
            String key = likeResult.get(0);
            String value = likeResult.get(1);

            kafkaTemplate.send(kafkaProducer.topic(), key.getBytes(), value.getBytes());
        }

    }

}

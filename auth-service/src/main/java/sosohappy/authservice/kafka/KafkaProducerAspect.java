package sosohappy.authservice.kafka;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class KafkaProducerAspect {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @AfterReturning(value = "@annotation(kafkaProducer)", returning = "result")
    public void handleKafkaProducer(JoinPoint joinPoint, KafkaProducer kafkaProducer, Object result){

        if(result instanceof Map){
            Map<String, String> resultMap = (Map<String, String>) result;
            kafkaTemplate.send(kafkaProducer.topic(), resultMap.get("key"), resultMap.get("value"));
        } else if (result instanceof String){
            String email = (String) joinPoint.getArgs()[0];
            kafkaTemplate.send(kafkaProducer.topic(), email, (String) result);
        }

    }

}

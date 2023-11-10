package sosohappy.authservice.kafka;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class KafkaProducerAspect {

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final ScheduledExecutorService scheduledExecutorService;

    @AfterReturning(value = "@annotation(kafkaProducer)", returning = "result")
    public void handleKafkaProducer(JoinPoint joinPoint, KafkaProducer kafkaProducer, Object result){

        if(kafkaProducer.topic().equals("accessToken")){
            String email = (String) joinPoint.getArgs()[0];

            kafkaTemplate.send(kafkaProducer.topic(), email.getBytes(), ((String) result).getBytes());

            scheduledExecutorService.schedule(
                    () -> {
                        kafkaTemplate.send("expired", email.getBytes(), null);
                    },
                    36000000,
                    TimeUnit.MILLISECONDS
            );
        }

        if(kafkaProducer.topic().equals("resign")){
            List<String> resultList = (List<String>) result;

            String email = resultList.get(0);
            String nickname = resultList.get(1);
            kafkaTemplate.send(kafkaProducer.topic(), email.getBytes(), nickname.getBytes());
        }

        if(kafkaProducer.topic().equals("deviceToken")){
            List<String> resultList = (List<String>) result;

            String email = resultList.get(0);
            String deviceToken = resultList.get(1);
            kafkaTemplate.send(kafkaProducer.topic(), email.getBytes(), deviceToken.getBytes());
        }

    }

}

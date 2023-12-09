package sosohappy.dmservice.kafka;

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

    @AfterReturning(value = "@annotation(kafkaProducer)", returning = "result")
    public void handleKafkaProducer(KafkaProducer kafkaProducer, Object result){

        if(kafkaProducer.topic().equals("directMessage")){
            List<String> resultList = (List<String>) result;

            String deviceToken = resultList.get(0);
            String messageDto = resultList.get(1);

            kafkaTemplate.send(kafkaProducer.topic(), deviceToken.getBytes(), messageDto.getBytes());
        }

    }

}


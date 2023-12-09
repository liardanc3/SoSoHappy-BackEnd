package sosohappy.dmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sosohappy.dmservice.domain.dto.FindDirectMessageFilter;
import sosohappy.dmservice.domain.dto.MessageDto;
import sosohappy.dmservice.kafka.KafkaProducer;
import sosohappy.dmservice.repository.MessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public List<MessageDto> findDirectMessage(FindDirectMessageFilter findDirectMessageFilter) {
        return messageRepository.findDirectMessage(findDirectMessageFilter);
    }

    public List<MessageDto> findMultipleDirectMessage(String sender) {
        return messageRepository.findMultipleDirectMessage(sender);
    }

    @KafkaProducer(topic = "directMessage")
    public List<String> produceDirectMessage(String deviceToken, String messageDto){
        return List.of(deviceToken, messageDto);
    }

}

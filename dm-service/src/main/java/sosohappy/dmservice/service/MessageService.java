package sosohappy.dmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sosohappy.dmservice.domain.collection.Message;
import sosohappy.dmservice.domain.dto.MessageDto;
import sosohappy.dmservice.domain.dto.FindDirectMessageFilter;
import sosohappy.dmservice.repository.MessageRepository;
import sosohappy.dmservice.util.Utils;

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

}

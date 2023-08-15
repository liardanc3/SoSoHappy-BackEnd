package sosohappy.dmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sosohappy.dmservice.repository.MessageRepository;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

}

package dev.sosohappy.monolithic.service;

import dev.sosohappy.monolithic.model.dto.*;
import dev.sosohappy.monolithic.repository.nosql.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

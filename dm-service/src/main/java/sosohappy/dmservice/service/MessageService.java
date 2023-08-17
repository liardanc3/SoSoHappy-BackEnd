package sosohappy.dmservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import sosohappy.dmservice.aspect.exception.MessageToJsonException;
import sosohappy.dmservice.domain.dto.MessageDto;
import sosohappy.dmservice.repository.MessageRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ObjectMapper jsonMapper;

    private Map<String, WebSocketSession> clientMap = new ConcurrentHashMap<>();

    public Mono<Void> handleMessage(WebSocketSession session) {
        return session.send(
                session.receive()
                        .map(webSocketMessage -> this.saveClient(webSocketMessage, session))
                        .map(this::textToMessageDto)
                        .map(MessageDto::getSender)
                        .map(session::textMessage)
        );
    }

    private WebSocketMessage saveClient(WebSocketMessage socketMessage, WebSocketSession session) {
        String sender = textToMessageDto(socketMessage).getSender();

        if(!clientMap.containsKey(sender)){
            clientMap.put(sender, session);
        }

        return socketMessage;
    }

    private MessageDto textToMessageDto(WebSocketMessage socketMessage) {
        try {
            return jsonMapper.readValue(socketMessage.getPayloadAsText(), MessageDto.class);
        } catch (JsonProcessingException e) {
            throw new MessageToJsonException(e.getMessage());
        }
    }
}

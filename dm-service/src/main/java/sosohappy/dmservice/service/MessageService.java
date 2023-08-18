package sosohappy.dmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import sosohappy.dmservice.domain.collection.Message;
import sosohappy.dmservice.domain.dto.MessageDto;
import sosohappy.dmservice.repository.MessageRepository;
import sosohappy.dmservice.util.Utils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final Utils utils;
    private final MessageRepository messageRepository;

    private final Map<String, String> nickNameToSessionIdMap;
    private final Map<String, WebSocketSession> sessionIdToSessionMap;

    public Mono<Void> handleSession(WebSocketSession session) {
        return session.receive()
                .doOnSubscribe(subscription -> saveSessionInfo(session))
                .map(this::handleMessage)
                .doOnNext(this::saveMessage)
                .then();
    }

    // ----------------------------------------------------------------------------- //

    private void saveMessage(WebSocketMessage webSocketMessage) {
        messageRepository.save(
                new Message(
                        utils.jsonToObject(webSocketMessage.getPayloadAsText(), MessageDto.class)
                )
        ).subscribe();
    }

    private void saveSessionInfo(WebSocketSession session) {
        nickNameToSessionIdMap.put(session.getHandshakeInfo().getUri().getQuery(), session.getId());
        sessionIdToSessionMap.put(session.getId(), session);
    }

    private WebSocketMessage handleMessage(WebSocketMessage webSocketMessage) {
        MessageDto messageDto = utils.jsonToObject(webSocketMessage.getPayloadAsText(), MessageDto.class);
        WebSocketSession receiverSession = getReceiverSession(messageDto);

        receiverSession.send(
                Mono.just(receiverSession.textMessage(webSocketMessage.getPayloadAsText()))
        ).subscribe();

        return webSocketMessage;
    }

    private WebSocketSession getReceiverSession(MessageDto messageDto) {
        String receiverSessionId = nickNameToSessionIdMap.get(messageDto.getReceiver());
        return sessionIdToSessionMap.get(receiverSessionId);
    }
}

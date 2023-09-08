package sosohappy.dmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sosohappy.dmservice.domain.collection.Message;
import sosohappy.dmservice.domain.dto.MessageDto;
import sosohappy.dmservice.domain.dto.FindDirectMessageFilter;
import sosohappy.dmservice.repository.MessageRepository;
import sosohappy.dmservice.util.Utils;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final Utils utils;
    private final MessageRepository messageRepository;

    private final HashMap<String, String> nickNameToSessionIdMap;
    private final HashMap<String, WebSocketSession> sessionIdToSessionMap;

    public Mono<Void> connectSessionAndSendMessage(WebSocketSession session) {
        return session.receive()
                .doOnSubscribe(subscription -> saveSessionInfo(session))
                .map(this::sendMessage)
                .onErrorContinue((err, arg) -> {})
                .doOnNext(this::saveDirectMessage)
                .then();
    }

    public Flux<MessageDto> findDirectMessage(FindDirectMessageFilter findDirectMessageFilter) {
        return messageRepository.findDirectMessage(findDirectMessageFilter);
    }

    public Flux<MessageDto> findMultipleDirectMessage(String sender) {
        return messageRepository.findMultipleDirectMessage(sender);
    }

    // ----------------------------------------------------------------------------- //

    private void saveDirectMessage(WebSocketMessage webSocketMessage) {
        messageRepository.save(
                new Message(
                        utils.jsonToObject(webSocketMessage.getPayloadAsText(), MessageDto.class)
                )
        ).subscribe();
    }

    private void saveSessionInfo(WebSocketSession session) {
        nickNameToSessionIdMap.put(session.getHandshakeInfo().getUri().getQuery().split("=")[1], session.getId());
        sessionIdToSessionMap.put(session.getId(), session);
    }

    private WebSocketMessage sendMessage(WebSocketMessage webSocketMessage) {
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

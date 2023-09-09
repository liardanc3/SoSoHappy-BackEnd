package sosohappy.noticeservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import sosohappy.noticeservice.kafka.KafkaConsumer;
import sosohappy.noticeservice.util.Utils;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class NoticeService {

    public static HashMap<String, String> nickNameToSessionIdMap = new HashMap<>();
    public static HashMap<String, WebSocketSession> sessionIdToSessionMap = new HashMap<>();

    public Mono<Void> connectSessionAndSendNotice(WebSocketSession session) {
        return session.receive()
                .doOnSubscribe(subscription -> saveSessionInfo(session))
                .then();
    }

    public void sendNotice(String targetNickname, String notice) {
        WebSocketSession receiverSession = getReceiverSession(targetNickname);

        receiverSession.send(
                Mono.just(receiverSession.textMessage(notice))
        ).subscribe();
    }

    // ------------------------------------------------------ //

    private void saveSessionInfo(WebSocketSession session) {
        nickNameToSessionIdMap.put(session.getHandshakeInfo().getUri().getQuery().split("=")[1], session.getId());
        sessionIdToSessionMap.put(session.getId(), session);
    }

    private WebSocketSession getReceiverSession(String targetNickname) {
        String receiverSessionId = nickNameToSessionIdMap.get(targetNickname);
        return sessionIdToSessionMap.get(receiverSessionId);
    }
}

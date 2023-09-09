package sosohappy.noticeservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final HashMap<String, String> nickNameToSessionIdMap;
    private final HashMap<String, WebSocketSession> sessionIdToSessionMap;

    public Mono<Void> connectSessionAndSendNotice(WebSocketSession session) {
        return session.receive()
                .doOnSubscribe(subscription -> saveSessionInfo(session))
                .then();
    }

    // ------------------------------------------------------ //

    private void saveSessionInfo(WebSocketSession session) {
        nickNameToSessionIdMap.put(session.getHandshakeInfo().getUri().getQuery().split("=")[1], session.getId());
        sessionIdToSessionMap.put(session.getId(), session);
    }


}

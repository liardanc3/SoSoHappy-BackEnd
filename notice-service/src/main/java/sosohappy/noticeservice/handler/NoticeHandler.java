package sosohappy.noticeservice.handler;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import sosohappy.noticeservice.service.NoticeService;

@Component
@RequiredArgsConstructor
public class NoticeHandler implements WebSocketHandler {

    private final NoticeService noticeService;

    @NotNull
    @Override
    public Mono<Void> handle(@NotNull WebSocketSession session) {
        return noticeService.connectSessionAndSendNotice(session);
    }

}

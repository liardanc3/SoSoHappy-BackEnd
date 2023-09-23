package sosohappy.dmservice.handler;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import sosohappy.dmservice.service.MessageService;

@Component
@RequiredArgsConstructor
public class MessageHandler implements WebSocketHandler {

    private final MessageService messageService;

    @NotNull
    @Override
    public Mono<Void> handle(@NotNull WebSocketSession session) {
        return messageService.connectSessionAndSendMessage(session);
    }

}

package sosohappy.dmservice.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import sosohappy.dmservice.domain.collection.Message;
import sosohappy.dmservice.domain.dto.MessageDto;
import sosohappy.dmservice.exception.custom.BadRequestException;
import sosohappy.dmservice.kafka.KafkaConsumer;
import sosohappy.dmservice.repository.MessageRepository;
import sosohappy.dmservice.service.MessageService;
import sosohappy.dmservice.util.Utils;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final Utils utils;
    private final MessageService messageService;
    private final MessageRepository messageRepository;

    private static final ConcurrentHashMap<String, WebSocketSession> sessionIdAndSessionMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> nicknameAndSessionIdMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try{
            String nickname = Objects.requireNonNull(session.getUri()).toString().split("nickname=")[1];

            nicknameAndSessionIdMap.put(nickname, session.getId());
            sessionIdAndSessionMap.put(session.getId(), session);
        } catch (Exception e){
            throw new BadRequestException();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try{
            String nickname = Objects.requireNonNull(session.getUri()).toString().split("nickname=")[1];

            nicknameAndSessionIdMap.remove(nickname);
            sessionIdAndSessionMap.remove(session.getId());
        } catch (Exception e){
            throw new BadRequestException();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        MessageDto messageDto = utils.jsonToObject(message.getPayload(), MessageDto.class);

        String receiver = messageDto.getReceiver();
        String sessionId = nicknameAndSessionIdMap.get(receiver);

        messageRepository.save(new Message(messageDto));

        if(sessionId != null){
            WebSocketSession receiverSession = sessionIdAndSessionMap.get(sessionId);
            receiverSession.sendMessage(message);
        } else {
            String deviceToken = KafkaConsumer.nicknameAndDeviceTokenMap.get(receiver);
            messageService.produceDirectMessage(deviceToken, message.getPayload());
        }
    }
}

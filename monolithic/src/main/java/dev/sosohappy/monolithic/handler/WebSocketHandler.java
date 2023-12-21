package dev.sosohappy.monolithic.handler;

import dev.sosohappy.monolithic.exception.custom.BadRequestException;
import dev.sosohappy.monolithic.model.collection.Message;
import dev.sosohappy.monolithic.model.dto.MessageDto;
import dev.sosohappy.monolithic.repository.nosql.MessageRepository;
import dev.sosohappy.monolithic.repository.rdbms.UserRepository;
import dev.sosohappy.monolithic.service.NoticeService;
import dev.sosohappy.monolithic.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final Utils utils;
    private final NoticeService noticeService;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private static final ConcurrentHashMap<String, WebSocketSession> sessionIdAndSessionMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> nicknameAndSessionIdMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        try{
            String nickname = Objects.requireNonNull(session.getUri()).toString().split("nickname=")[1];

            nicknameAndSessionIdMap.put(nickname, session.getId());
            sessionIdAndSessionMap.put(session.getId(), session);
        } catch (Exception e){
            session.close(CloseStatus.BAD_DATA);
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
            String deviceToken = userRepository.findDeviceTokenByNickname(receiver);
            noticeService.sendDirectMessage(deviceToken, message.getPayload());
        }
    }
}

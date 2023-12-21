package dev.sosohappy.monolithic.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import dev.sosohappy.monolithic.model.dto.MessageDto;
import dev.sosohappy.monolithic.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final FirebaseMessaging firebaseMessaging;
    private final Utils utils;

    @SneakyThrows
    public void sendNotice(String srcNickname, String dstNickname, Long date, String deviceToken) {
        Message message = Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle("소소해피")
                                .setBody(srcNickname + "님이 " + dstNickname + "님의 행복을 응원합니다\uD83E\uDE77")
                                .build()
                )
                .setToken(deviceToken)
                .putData("date", date.toString())
                .build();

        firebaseMessaging.send(message);
    }

    public void sendDirectMessage(String deviceToken, String messageDtoStr) throws FirebaseMessagingException {
        MessageDto messageDto = utils.jsonToObject(messageDtoStr, MessageDto.class);

        String sender = messageDto.getSender();
        String text = messageDto.getText();

        Message message = Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(sender)
                                .setBody(text.length() < 30 ? text : text.substring(0, 25) + "...")
                                .build()
                )
                .setToken(deviceToken)
                .putData("messageDto", messageDtoStr)
                .build();

        firebaseMessaging.send(message);
    }
}

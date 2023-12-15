package sosohappy.noticeservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import sosohappy.noticeservice.dto.MessageDto;
import sosohappy.noticeservice.util.Utils;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final FirebaseMessaging firebaseMessaging;
    private final Utils utils;

    public Mono<Void> sendNotice(String srcNickname, String dstNickname, Long date, String deviceToken) {
        return Mono.fromRunnable(() -> {
            Message message = Message.builder()
                    .setNotification(
                            Notification.builder()
                                    .setTitle("소소해피")
                                    .setBody(srcNickname + "님이 " + dstNickname + "님의 행복을 응원합니다.")
                                    .build()
                    )
                    .setToken(deviceToken)
                    .putData("date", date.toString())
                    .build();

            firebaseMessaging.sendAsync(message);
        });
    }

    public Mono<Void> sendDirectMessage(String deviceToken, String messageDtoStr) {
        return Mono.defer(() -> {
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

            firebaseMessaging.sendAsync(message);

            return Mono.empty();
        });
    }
}

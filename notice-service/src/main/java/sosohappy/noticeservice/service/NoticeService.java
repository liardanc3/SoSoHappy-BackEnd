package sosohappy.noticeservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;
import sosohappy.noticeservice.dto.MessageDto;
import sosohappy.noticeservice.util.Utils;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final FirebaseMessaging firebaseMessaging;
    private final Utils utils;

    public Mono<Void> sendNotice(String srcNickname, Long date, String deviceToken) {
        return Mono.defer(() -> {
            Message message = Message.builder()
                    .setNotification(
                            Notification.builder()
                                    .setTitle("좋아요 알림")
                                    .setBody(srcNickname + " 님이 내 피드에 좋아요를 눌렀습니다.")
                                    .build()
                    )
                    .setToken(deviceToken)
                    .putData("date", date.toString())
                    .build();

            firebaseMessaging.sendAsync(message);

            return Mono.empty();
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

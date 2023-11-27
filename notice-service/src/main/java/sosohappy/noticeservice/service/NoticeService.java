package sosohappy.noticeservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import sosohappy.noticeservice.kafka.KafkaConsumer;
import sosohappy.noticeservice.util.Utils;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final FirebaseMessaging firebaseMessaging;

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

}

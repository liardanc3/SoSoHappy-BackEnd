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

    public void sendNotice() throws FirebaseMessagingException {
        Message message = Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle("a")
                                .setBody("b")
                                .build()
                )
                .setTopic("like")
                .build();


        firebaseMessaging.sendAsync(message);
    }

}

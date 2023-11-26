package sosohappy.noticeservice;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import sosohappy.noticeservice.service.NoticeService;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final NoticeService noticeService;

    @GetMapping("/test-actuator")
    public Mono<Void> test() throws FirebaseMessagingException {
        return noticeService.sendNotice("hello", 2023112610101010L, "asdasd");
    }
}

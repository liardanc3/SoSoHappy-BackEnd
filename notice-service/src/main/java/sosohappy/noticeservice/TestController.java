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
    public void test() throws FirebaseMessagingException {
        noticeService.sendNotice();
    }
}

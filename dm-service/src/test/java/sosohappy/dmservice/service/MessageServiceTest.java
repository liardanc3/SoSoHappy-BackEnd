package sosohappy.dmservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sosohappy.dmservice.repository.MessageRepository;

@SpringBootTest
class MessageServiceTest {

    @Autowired
    private MessageRepository messageRepository;

}
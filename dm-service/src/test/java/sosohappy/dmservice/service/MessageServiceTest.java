package sosohappy.dmservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sosohappy.dmservice.collection.Message;
import sosohappy.dmservice.repository.MessageRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageServiceTest {

    @Autowired
    private MessageRepository messageRepository;

}
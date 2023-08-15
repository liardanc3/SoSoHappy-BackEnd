package sosohappy.dmservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import sosohappy.dmservice.service.MessageService;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class MessageController {

    private final MessageService messageService;


}

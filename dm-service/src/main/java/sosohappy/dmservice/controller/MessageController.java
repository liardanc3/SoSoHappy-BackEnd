package sosohappy.dmservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import sosohappy.dmservice.domain.dto.FindDirectMessageFilter;
import sosohappy.dmservice.domain.dto.MessageDto;
import sosohappy.dmservice.service.MessageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/findDirectMessage")
    public List<MessageDto> findDirectMessage(@ModelAttribute @Valid FindDirectMessageFilter findDirectMessageFilter){
        return messageService.findDirectMessage(findDirectMessageFilter);
    }

    @PostMapping("/findMultipleDirectMessage")
    public List<MessageDto> findMultipleDirectMessage(@RequestPart String sender){
        return messageService.findMultipleDirectMessage(sender);
    }



}

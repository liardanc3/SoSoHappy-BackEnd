package sosohappy.dmservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import sosohappy.dmservice.domain.dto.ExceptionDto;
import sosohappy.dmservice.domain.dto.MessageDto;
import sosohappy.dmservice.domain.dto.FindDirectMessageFilter;
import sosohappy.dmservice.service.MessageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dm-service")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/searchDirectMessage")
    public Flux<MessageDto> searchDirectMessage(@ModelAttribute FindDirectMessageFilter findDirectMessageFilter){
        return messageService.findDirectMessage(findDirectMessageFilter);
    }

}

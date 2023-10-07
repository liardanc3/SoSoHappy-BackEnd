package sosohappy.dmservice.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sosohappy.dmservice.exception.annotation.ConvertException;
import sosohappy.dmservice.exception.custom.FindMessageException;
import sosohappy.dmservice.domain.dto.MessageDto;
import sosohappy.dmservice.domain.dto.FindDirectMessageFilter;
import sosohappy.dmservice.service.MessageService;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @ConvertException(target = FindMessageException.class)
    @PostMapping("/findDirectMessage")
    public Flux<MessageDto> findDirectMessage(@ModelAttribute FindDirectMessageFilter findDirectMessageFilter){
        return messageService.findDirectMessage(findDirectMessageFilter);
    }

    @ConvertException(target = FindMessageException.class)
    @PostMapping("/findMultipleDirectMessage")
    public Flux<MessageDto> findMultipleDirectMessage(@RequestPart String sender){
        return messageService.findMultipleDirectMessage(sender);
    }

    @GetMapping("/test-actuator")
    public Mono<String> test(){
        return Mono.just("dm-service on");
    }
}

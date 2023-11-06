package sosohappy.dmservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sosohappy.dmservice.domain.dto.FindDirectMessageFilter;
import sosohappy.dmservice.domain.dto.MessageDto;
import sosohappy.dmservice.service.MessageService;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/findDirectMessage")
    public Flux<MessageDto> findDirectMessage(@ModelAttribute FindDirectMessageFilter findDirectMessageFilter){
        return messageService.findDirectMessage(findDirectMessageFilter);
    }

    @PostMapping("/findMultipleDirectMessage")
    public Flux<MessageDto> findMultipleDirectMessage(@RequestPart String sender){
        return messageService.findMultipleDirectMessage(sender);
    }

    @GetMapping("/test-actuator")
    public Mono<String> test(){
        return Mono.just("dm-service on");
    }
}

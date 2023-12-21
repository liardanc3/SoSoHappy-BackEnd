package dev.sosohappy.monolithic.controller;

import dev.sosohappy.monolithic.model.dto.FindDirectMessageFilter;
import dev.sosohappy.monolithic.model.dto.FindMultipleDirectMessageFilter;
import dev.sosohappy.monolithic.model.dto.MessageDto;
import dev.sosohappy.monolithic.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dm-service")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/findDirectMessage")
    public List<MessageDto> findDirectMessage(@ModelAttribute @Valid FindDirectMessageFilter findDirectMessageFilter){
        return messageService.findDirectMessage(findDirectMessageFilter);
    }

    @PostMapping("/findMultipleDirectMessage")
    public List<MessageDto> findMultipleDirectMessage(@ModelAttribute @Valid FindMultipleDirectMessageFilter filter){
        return messageService.findMultipleDirectMessage(filter.getSender());
    }

}

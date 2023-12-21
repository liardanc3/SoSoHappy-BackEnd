package dev.sosohappy.monolithic.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class FindDirectMessageFilter {

    @NotEmpty
    private String sender;

    @NotEmpty
    private String receiver;

    private String messageRoomId;

    @NotNull
    private Long timeBoundary;

    private Integer messageCnt;

    public FindDirectMessageFilter(String sender, String receiver, Long timeBoundary, Integer messageCnt) {
        this.sender = sender;
        this.receiver = receiver;
        this.timeBoundary = timeBoundary;
        this.messageCnt = messageCnt == null ? 20 : messageCnt;

        this.messageRoomId = Stream.of(sender, receiver)
                .sorted()
                .collect(Collectors.joining(","));
    }
}

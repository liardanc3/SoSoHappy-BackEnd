package sosohappy.dmservice.domain.dto;

import lombok.Data;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class FindDirectMessageFilter {

    private String sender;
    private String receiver;
    private String messageRoomId;

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

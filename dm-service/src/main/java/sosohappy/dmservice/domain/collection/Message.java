package sosohappy.dmservice.domain.collection;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sosohappy.dmservice.domain.dto.MessageDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
@Document(collection = "message")
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    private ObjectId id;

    private String messageRoomId;

    private Long createdDate;

    private String sender;
    private String receiver;

    private String text;

    public Message(MessageDto messageDto){
        this.createdDate = messageDto.getCreatedDate();

        this.sender = messageDto.getSender();
        this.receiver = messageDto.getReceiver();
        
        this.text = messageDto.getText();

        this.messageRoomId = Stream.of(sender, receiver)
                .sorted()
                .collect(Collectors.joining(","));
    }
}

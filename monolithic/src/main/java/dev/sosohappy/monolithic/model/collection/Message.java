package dev.sosohappy.monolithic.model.collection;

import dev.sosohappy.monolithic.model.dto.MessageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private Long date;

    private String sender;
    private String receiver;

    private String text;

    public Message(MessageDto messageDto){
        this.date = messageDto.getDate();

        this.sender = messageDto.getSender();
        this.receiver = messageDto.getReceiver();
        
        this.text = messageDto.getText();

        this.messageRoomId = Stream.of(sender, receiver)
                .sorted()
                .collect(Collectors.joining(","));
    }
}

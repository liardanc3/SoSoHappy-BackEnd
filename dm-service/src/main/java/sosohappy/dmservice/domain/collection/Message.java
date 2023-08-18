package sosohappy.dmservice.domain.collection;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sosohappy.dmservice.domain.dto.MessageDto;

@Getter
@Document(collection = "message")
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    private ObjectId id;

    @CreatedDate
    private String createdDate;

    private String sender;
    private String receiver;

    private String text;

    public Message(MessageDto messageDto){
        this.sender = messageDto.getSender();
        this.receiver = messageDto.getReceiver();
        
        this.text = messageDto.getText();
    }
}

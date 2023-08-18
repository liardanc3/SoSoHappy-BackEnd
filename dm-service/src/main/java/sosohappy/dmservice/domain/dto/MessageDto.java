package sosohappy.dmservice.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import sosohappy.dmservice.domain.collection.Message;

@Data
@NoArgsConstructor
public class MessageDto {

    private String sender;
    private String receiver;

    private Long createdDate;

    private String text;

    public MessageDto(Message message){
        this.sender = message.getSender();
        this.receiver = message.getReceiver();

        this.createdDate = message.getCreatedDate();
        this.text = message.getText();
    }
}

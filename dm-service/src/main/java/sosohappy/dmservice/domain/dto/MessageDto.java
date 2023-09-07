package sosohappy.dmservice.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import sosohappy.dmservice.domain.collection.Message;

@Data
@NoArgsConstructor
public class MessageDto {

    private String sender;
    private String receiver;

    private Long date;

    private String text;

    public MessageDto(Message message){
        this.sender = message.getSender();
        this.receiver = message.getReceiver();

        this.date = message.getDate();
        this.text = message.getText();
    }
}

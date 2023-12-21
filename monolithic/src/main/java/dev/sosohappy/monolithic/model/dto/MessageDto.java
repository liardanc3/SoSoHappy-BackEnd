package dev.sosohappy.monolithic.model.dto;

import dev.sosohappy.monolithic.model.collection.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

package sosohappy.dmservice.domain.dto;

import lombok.Data;

@Data
public class MessageDto {

    private String sender;
    private String receiver;

    private String createdDate;

    private String text;
}

package sosohappy.dmservice.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {

    private String sender;
    private String receiver;

    private LocalDateTime createdDate;

    private String text;
}

package sosohappy.dmservice.domain.collection;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Document(collection = "message")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {

    @Id
    private ObjectId id;

    @CreatedDate
    private String createdDate;

    private String sender;
    private String receiver;

    private String text;
}

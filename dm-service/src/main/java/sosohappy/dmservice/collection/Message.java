package sosohappy.dmservice.collection;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document(collection = "message")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {

    @Id
    private ObjectId id;

    @CreatedDate
    private LocalDateTime createdDate;

    private String sender;
    private String receiver;

    private String text;
    private byte[] fileData;

}

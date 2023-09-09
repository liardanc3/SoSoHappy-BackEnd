package sosohappy.noticeservice.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Notice<T> {

    private String topic;

    private T data;
}

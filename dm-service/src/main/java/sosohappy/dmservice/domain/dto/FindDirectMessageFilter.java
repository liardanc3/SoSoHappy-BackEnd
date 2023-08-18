package sosohappy.dmservice.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindDirectMessageFilter {

    private String sender;
    private String receiver;

    private Long timeBoundary;

    private Integer messageCnt;
}

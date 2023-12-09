package sosohappy.dmservice.repository;

import sosohappy.dmservice.domain.dto.FindDirectMessageFilter;
import sosohappy.dmservice.domain.dto.MessageDto;

import java.util.List;

public interface MessageQueryRepository {

    List<MessageDto> findDirectMessage(FindDirectMessageFilter findDirectMessageFilter);

    List<MessageDto> findMultipleDirectMessage(String sender);
}

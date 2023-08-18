package sosohappy.dmservice.repository;

import reactor.core.publisher.Flux;
import sosohappy.dmservice.domain.dto.FindDirectMessageFilter;
import sosohappy.dmservice.domain.dto.MessageDto;

public interface MessageQueryRepository {

    Flux<MessageDto> findDirectMessage(FindDirectMessageFilter findDirectMessageFilter);
}

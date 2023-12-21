package dev.sosohappy.monolithic.repository.nosql;


import dev.sosohappy.monolithic.model.dto.FindDirectMessageFilter;
import dev.sosohappy.monolithic.model.dto.MessageDto;

import java.util.List;

public interface MessageQueryRepository {

    List<MessageDto> findDirectMessage(FindDirectMessageFilter findDirectMessageFilter);

    List<MessageDto> findMultipleDirectMessage(String sender);
}

package dev.sosohappy.monolithic.repository.nosql;

import dev.sosohappy.monolithic.model.collection.Message;
import dev.sosohappy.monolithic.model.dto.FindDirectMessageFilter;
import dev.sosohappy.monolithic.model.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MessageQueryRepositoryImpl implements MessageQueryRepository{

    private final MongoTemplate mongoTemplate;

    public List<MessageDto> findDirectMessage(FindDirectMessageFilter findDirectMessageFilter){
        String messageRoomId = findDirectMessageFilter.getMessageRoomId();
        Long timeBoundary = findDirectMessageFilter.getTimeBoundary();
        Integer messageCnt = findDirectMessageFilter.getMessageCnt();

        List<MessageDto> directMessageList = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(
                                new Criteria().andOperator(
                                        Criteria.where("messageRoomId").is(messageRoomId),
                                        Criteria.where("date").lt(timeBoundary)
                                )
                        ),
                        Aggregation.sort(Sort.Direction.DESC, "date"),
                        Aggregation.limit(messageCnt)
                ),
                "message",
                Message.class
        ).getMappedResults().stream().map(MessageDto::new).collect(Collectors.toList());

        Collections.reverse(directMessageList);

        return directMessageList;
    }

    public List<MessageDto> findMultipleDirectMessage(String sender){
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(
                                new Criteria().orOperator(
                                        Criteria.where("sender").is(sender),
                                        Criteria.where("receiver").is(sender)
                                )
                        ),
                        Aggregation.group("messageRoomId").last("$$ROOT").as("lastDirectMessage"),
                        Aggregation.replaceRoot("lastDirectMessage"),
                        Aggregation.sort(Sort.Direction.DESC, "date")
                ),
                "message",
                Message.class
            ).getMappedResults().stream().map(MessageDto::new).collect(Collectors.toList());
    }
}

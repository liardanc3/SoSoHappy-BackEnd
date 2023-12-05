package sosohappy.dmservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import sosohappy.dmservice.domain.collection.Message;
import sosohappy.dmservice.domain.dto.FindDirectMessageFilter;
import sosohappy.dmservice.domain.dto.MessageDto;

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

        return mongoTemplate.find(
                new Query()
                        .addCriteria(Criteria.where("messageRoomId").is(messageRoomId))
                        .addCriteria(Criteria.where("date").lt(timeBoundary))
                        .limit(messageCnt),

                        Message.class
                )
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
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

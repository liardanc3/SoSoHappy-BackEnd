package sosohappy.dmservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import sosohappy.dmservice.domain.collection.Message;
import sosohappy.dmservice.domain.dto.FindDirectMessageFilter;
import sosohappy.dmservice.domain.dto.MessageDto;

@Repository
@RequiredArgsConstructor
public class MessageQueryRepositoryImpl implements MessageQueryRepository{

    private final ReactiveMongoTemplate mongoTemplate;

    public Flux<MessageDto> findDirectMessage(FindDirectMessageFilter findDirectMessageFilter){
        String sender = findDirectMessageFilter.getSender();
        String receiver = findDirectMessageFilter.getReceiver();
        Long timeBoundary = findDirectMessageFilter.getTimeBoundary();
        Integer messageCnt = findDirectMessageFilter.getMessageCnt();

        return mongoTemplate.find(
                new Query()
                        .addCriteria(Criteria.where("createdDate").lte(timeBoundary))
                        .addCriteria(
                                new Criteria().orOperator(
                                        Criteria.where("sender").is(sender).and("receiver").is(receiver),
                                        Criteria.where("sender").is(receiver).and("receiver").is(sender)
                                ))
                        .limit(messageCnt),

                        Message.class
                )
                .map(MessageDto::new);
    }
}

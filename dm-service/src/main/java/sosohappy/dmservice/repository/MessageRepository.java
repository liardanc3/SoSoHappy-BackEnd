package sosohappy.dmservice.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import sosohappy.dmservice.domain.collection.Message;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<Message, Long>, MessageQueryRepository{

}

package sosohappy.dmservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import sosohappy.dmservice.collection.Message;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<Message, Long> {

}

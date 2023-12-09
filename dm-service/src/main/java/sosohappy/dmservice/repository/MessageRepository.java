package sosohappy.dmservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sosohappy.dmservice.domain.collection.Message;

@Repository
public interface MessageRepository extends MongoRepository<Message, Long>, MessageQueryRepository{

}

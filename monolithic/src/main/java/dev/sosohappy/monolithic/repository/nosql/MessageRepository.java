package dev.sosohappy.monolithic.repository.nosql;

import dev.sosohappy.monolithic.model.collection.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, Long>, MessageQueryRepository{

}

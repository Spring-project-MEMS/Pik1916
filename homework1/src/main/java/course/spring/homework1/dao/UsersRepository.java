package course.spring.homework1.dao;

import course.spring.homework1.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersRepository extends MongoRepository<User,String> {
}

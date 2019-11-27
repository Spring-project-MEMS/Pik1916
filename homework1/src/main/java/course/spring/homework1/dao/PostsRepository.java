package course.spring.homework1.dao;

import course.spring.homework1.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostsRepository extends MongoRepository<Post,String> {
}

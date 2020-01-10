package course.spring.webmvc.dao;

import course.spring.webmvc.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostsRepository extends MongoRepository<Post,String> {
}

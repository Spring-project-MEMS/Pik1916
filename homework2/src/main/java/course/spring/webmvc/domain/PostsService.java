package course.spring.webmvc.domain;

import course.spring.webmvc.model.Post;

import java.util.List;
import java.util.stream.Stream;

public interface PostsService {
    List<Post> findAllPosts();
    Stream<Post> findLast15();
    Post findPostById(String postId);
    Post addPost(Post post);
    Post updatePost(Post post);
    Post removePost(String postId);
    long count();
}

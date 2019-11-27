package course.spring.homework1.domain;

import course.spring.homework1.model.Post;

import java.util.List;

public interface PostsService {
    List<Post> findAllPosts();
    Post findPostById(String postId);
    Post addPost(Post post);
    Post updatePost(Post post);
    Post remove(String postId);
}

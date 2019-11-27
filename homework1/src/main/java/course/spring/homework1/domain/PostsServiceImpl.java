package course.spring.homework1.domain;

import course.spring.homework1.dao.PostsRepository;
import course.spring.homework1.exception.NonexistingEntityException;
import course.spring.homework1.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostsServiceImpl implements PostsService {
    @Autowired
    private PostsRepository postRepo;

    @Override
    public List<Post> findAllPosts() {
        return postRepo.findAll();
    }

    @Override
    public Post findPostById(String postId) {
        return postRepo.findById(postId).orElseThrow(() -> new NonexistingEntityException(String.format("Post with id '%s' doesn't exist.", postId)));
    }

    @Override
    public Post addPost(Post post) {
        return postRepo.insert(post);
    }

    @Override
    public Post updatePost(Post post) {
        Optional<Post> oldPost = postRepo.findById(post.getId());
        if (!oldPost.isPresent())
        {
            throw new NonexistingEntityException(String.format("Post with id '%s' doesn't exist.", post.getId()));
        }
        post.setPublished(oldPost.get().getPublished());
        return postRepo.save(post);
    }

    @Override
    public Post remove(String postId) {
        Optional<Post> oldPost = postRepo.findById(postId);
        if (!oldPost.isPresent())
        {
            throw new NonexistingEntityException(String.format("Post with id '%s' doesn't exist.", postId));
        }
        postRepo.deleteById(postId);
        return oldPost.get();
    }
}

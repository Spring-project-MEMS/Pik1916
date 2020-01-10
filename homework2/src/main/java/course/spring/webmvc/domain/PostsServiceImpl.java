package course.spring.webmvc.domain;

import course.spring.webmvc.dao.PostsRepository;
import course.spring.webmvc.exception.NonexistingEntityException;
import course.spring.webmvc.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class PostsServiceImpl implements PostsService {
    @Autowired
    private PostsRepository postRepo;

    @Override
    @PostFilter("filterObject.author.id == authentication.principal.id or hasRole('ADMIN')")
    public List<Post> findAllPosts() {
        return postRepo.findAll();
    }

    @Override
    public Stream<Post> findLast15() {
        return findAllPosts().stream().sorted((o1, o2) -> -o1.getPublished().compareTo(o2.getPublished())).limit(15);
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
    public Post removePost(String postId) {
        Optional<Post> oldPost = postRepo.findById(postId);
        if (!oldPost.isPresent())
        {
            throw new NonexistingEntityException(String.format("Post with id '%s' doesn't exist.", postId));
        }
        postRepo.deleteById(postId);
        return oldPost.get();
    }

    @Override
    public long count() {
        return postRepo.count();
    }
}

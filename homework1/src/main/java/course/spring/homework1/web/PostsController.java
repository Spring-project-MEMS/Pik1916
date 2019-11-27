package course.spring.homework1.web;

import course.spring.homework1.domain.PostsService;
import course.spring.homework1.exception.InvalidEntityException;
import course.spring.homework1.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    @Autowired
    PostsService postsService;

    @GetMapping
    public List<Post> getPosts() {
        return postsService.findAllPosts();
    }

    @PostMapping
    public ResponseEntity<Post> addPost(@Valid @RequestBody Post post, BindingResult bindingResult)
    {
        if(bindingResult.hasFieldErrors()) {
            String  message = bindingResult.getFieldErrors().stream()
                    .map(err -> String.format("Invalid '%s' -> '%s': %s\n",
                            err.getField(), err.getRejectedValue(), err.getDefaultMessage()))
                    .reduce("", (acc, errStr) -> acc + errStr );
            throw new InvalidEntityException(message);
        }
        if(post.getStatus() != null && !post.getStatus().equals("active") && !post.getStatus().equals("unactive")){
            throw new InvalidEntityException(String.format("Post status ='%s' is invalid",post.getStatus()));
        }
        Post created = postsService.addPost(post);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
        .pathSegment("{postId}").build(created.getId())).body(created);
    }

    @GetMapping("{postId}")
    public Post getPostById(@PathVariable String postId)
    {
        return postsService.findPostById(postId);
    }

    @PutMapping("{postId}")
    public  Post updateArticle(@PathVariable String postId, @Valid @RequestBody Post post, BindingResult bindingResult){
        if(bindingResult.hasFieldErrors()) {
            String  message = bindingResult.getFieldErrors().stream()
                    .map(err -> String.format("Invalid '%s' -> '%s': %s\n",
                            err.getField(), err.getRejectedValue(), err.getDefaultMessage()))
                    .reduce("", (acc, errStr) -> acc + errStr );
            throw new InvalidEntityException(message);
        }
        if(!postId.equals(post.getId())){
            throw new InvalidEntityException(String.format("Entity ID = '%s' is different from URL resource ID='%s'",post.getId(),postId));
        }
        if(post.getStatus() != null && !post.getStatus().equals("active") && !post.getStatus().equals("unactive")){
            throw new InvalidEntityException(String.format("Post status ='%s' is invalid",post.getStatus()));
        }
        return postsService.updatePost(post);
    }

    @DeleteMapping("{postId}")
    public Post removePost(@PathVariable String postId)
    {
        return postsService.remove(postId);
    }
}

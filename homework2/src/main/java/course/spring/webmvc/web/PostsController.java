package course.spring.webmvc.web;

import course.spring.webmvc.domain.PostsService;
import course.spring.webmvc.exception.InvalidEntityException;
import course.spring.webmvc.model.Post;
import course.spring.webmvc.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/posts")
@Slf4j
public class PostsController {
    private static final String UPLOADS_DIR = "tmp";

    @Autowired
    private PostsService postsService;


    @GetMapping
    public String getPosts(Model model) {
        model.addAttribute("path", "posts");
        model.addAttribute("posts", postsService.findAllPosts());
        model.addAttribute("filter", false);
        log.debug("GET Posts: " + postsService.findAllPosts());
        return "posts";
    }

    @GetMapping("/lastposts")
    public String getLast15Posts(Model model) {
        model.addAttribute("path", "lastposts");
        model.addAttribute("posts", postsService.findLast15().collect(Collectors.toList()));
        model.addAttribute("filter", true);
        log.debug("GET Last 15 Posts: " + postsService.findAllPosts());
        return "posts";
    }

    @GetMapping("/onlyactive")
    public String getOnlyActivePosts(Model model) {
        model.addAttribute("path", "lastposts");
        model.addAttribute("posts", postsService.findLast15().filter(p -> p.getStatus().equals("active")).collect(Collectors.toList()));
        model.addAttribute("filter", true);
        return "posts";
    }

    @GetMapping("/onlyinactive")
    public String getOnlyInactivePosts(Model model) {
        model.addAttribute("path", "lastposts");
        model.addAttribute("posts", postsService.findLast15().filter(p -> p.getStatus().equals("inactive")).collect(Collectors.toList()));
        model.addAttribute("filter", true);
        return "posts";
    }

    @PostMapping("/post-form")
    public String addPost(@Valid @ModelAttribute ("post") Post post,
                          BindingResult errors,
//                             @RequestParam(name = "cancel", required = false) String cancelBtn,
                          @RequestParam("file") MultipartFile file,
                          Model model, Authentication authentication) {
//        if(cancelBtn != null) return "redirect:/posts";
        if (errors.hasErrors()) {
            log.info(errors.getAllErrors().toString());
            List<String> errorMessages = errors.getAllErrors().stream().map(err -> {
                ConstraintViolation cv = err.unwrap(ConstraintViolation.class);
                return String.format("Error in '%s' - invalid value: '%s'", cv.getPropertyPath(), cv.getInvalidValue());
            }).collect(Collectors.toList());

            model.addAttribute("myErrors", errorMessages);
            model.addAttribute("fileError", null);
            return "post-form";
        } else {
            log.info("POST Post: " + post);
            if (!file.isEmpty() && file.getOriginalFilename().length() > 0) {
                if (Pattern.matches("\\w+\\.(jpg|png)", file.getOriginalFilename())) {
                    handleMultipartFile(file);
                    post.setPictureUrl(file.getOriginalFilename());
                } else {
                    model.addAttribute("myErrors", null);
                    model.addAttribute("fileError", "Submit picture [.jpg, .png]");
                    return "post-form";
                }
            }
            if (post.getId() == null) {
                log.info("ADD New Post: " + post);
                User author = (User) authentication.getPrincipal();
                post.setAuthor(author);
                postsService.addPost(post);
            } else {
                log.info("UPDATE post: " + post);
                User author = (User) authentication.getPrincipal();
                post.setAuthor(author);
                postsService.updatePost(post);
            }
            return "redirect:/posts";
        }
    }

    @GetMapping("/post-form")
    public String getPostForm(@ModelAttribute ("post") Post post, ModelMap model,
                                 @RequestParam(value="mode", required=false) String mode,
                                 @RequestParam(value="postId", required=false) String postId){
        String title = "Add New Post";
        if("edit".equals(mode)) {
            post = postsService.findPostById(postId);
            model.addAttribute("post", post);
            title = "Edit Post";

        }

        model.addAttribute("path", "post-form");
        model.addAttribute("title", title);
        return "post-form";
    }


    @PostMapping(params = "edit")
    public String editPost(@RequestParam("edit") String editId, Model model){
        log.info("Editing post: " + editId);
        return "redirect:/posts/post-form?mode=edit&postId=" + editId;
    }

    @PostMapping(params = "delete")
    public String deletePost(@RequestParam("delete") String deleteId, Model model){
        log.info("Deleting post: " + deleteId);
        postsService.removePost(deleteId);
        return "redirect:/posts";
    }

    private void handleMultipartFile(MultipartFile file) {
        String name = file.getOriginalFilename();
        long size = file.getSize();
        log.info("File: " + name + ", Size: " + size);
        try {
            File currentDir = new File(UPLOADS_DIR);
            if(!currentDir.exists()) {
                currentDir.mkdirs();
            }
            String path = currentDir.getAbsolutePath() + "/" + file.getOriginalFilename();
            path = new File(path).getAbsolutePath();
            log.info(path);
            File f = new File(path);
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(f));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

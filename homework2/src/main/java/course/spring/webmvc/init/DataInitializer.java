package course.spring.webmvc.init;

import course.spring.webmvc.domain.PostsService;
import course.spring.webmvc.domain.UsersService;
import course.spring.webmvc.model.Post;
import course.spring.webmvc.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private PostsService postsService;

    @Autowired
    private UsersService usersService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing application...");
        if (usersService.count() == 0) {
            User user = new User("admin","blog_admin@gmail.com","admin","ROLE_ADMIN");
            log.info("Creating Admin User: ", user.getUsername());
            usersService.addUser(user);
        }
    }

}

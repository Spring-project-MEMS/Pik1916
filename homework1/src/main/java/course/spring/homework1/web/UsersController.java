package course.spring.homework1.web;

import course.spring.homework1.domain.UsersService;
import course.spring.homework1.exception.InvalidEntityException;
import course.spring.homework1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @GetMapping
    public List<User> getAllUsers(){
        return usersService.findAllUsers();
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user, BindingResult bindingResult)
    {
        if(bindingResult.hasFieldErrors()) {
            String  message = bindingResult.getFieldErrors().stream()
                    .map(err -> String.format("Invalid '%s' -> '%s': %s\n",
                            err.getField(), err.getRejectedValue(), err.getDefaultMessage()))
                    .reduce("", (acc, errStr) -> acc + errStr );
            throw new InvalidEntityException(message);
        }

        if(user.getRole() != null && !user.getRole().equals("Administrator") && !user.getRole().equals("Blogger")){
            throw new InvalidEntityException(String.format("User's role ='%s' is invalid",user.getRole()));
        }
        User created = usersService.addUser(user);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .pathSegment("{userId}").build(created.getId())).body(created);
    }

    @GetMapping("{userId}")
    public User getUserById(@PathVariable String userId)
    {
        return usersService.findUserByIs(userId);
    }

    @PutMapping("{userId}")
    public User updateUser(@PathVariable String userId,@Valid @RequestBody User user,BindingResult bindingResult)
    {
        if(bindingResult.hasFieldErrors()) {
            String  message = bindingResult.getFieldErrors().stream()
                    .map(err -> String.format("Invalid '%s' -> '%s': %s\n",
                            err.getField(), err.getRejectedValue(), err.getDefaultMessage()))
                    .reduce("", (acc, errStr) -> acc + errStr );
            throw new InvalidEntityException(message);
        }
        if(user.getRole() != null && !user.getRole().equals("Administrator") && !user.getRole().equals("Blogger")){
            throw new InvalidEntityException(String.format("User's role ='%s' is invalid",user.getRole()));
        }
        if(!userId.equals(user.getId()))
        {
            throw new InvalidEntityException(String.format("Entity ID = '%s' is different from URL resource ID='%s'",user.getId(),userId));
        }
        return usersService.updateUser(user);
    }

    @DeleteMapping("{userId}")
    public User removeUser(@PathVariable String userId)
    {
        return usersService.removeUser(userId);
    }
}

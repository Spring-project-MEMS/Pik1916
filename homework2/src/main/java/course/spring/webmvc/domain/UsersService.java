package course.spring.webmvc.domain;

import course.spring.webmvc.model.User;

import java.util.List;

public interface UsersService {
    List<User> findAllUsers();
    User findUserByIs(String userId);
    User findByUsername(String username);
    User findByEmail(String email);
    User addUser(User user);
    User updateUser(User user);
    User removeUser(String userId);
    User activateUser(String userId);
    User deactivateUser(String userId);
    long count();
}

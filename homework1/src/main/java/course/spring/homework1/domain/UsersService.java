package course.spring.homework1.domain;

import course.spring.homework1.model.User;

import java.util.List;

public interface UsersService {
    List<User> findAllUsers();
    User findUserByIs(String userId);
    User addUser(User user);
    User updateUser(User user);
    User removeUser(String userId);
}

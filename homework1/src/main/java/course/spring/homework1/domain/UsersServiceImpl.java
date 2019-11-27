package course.spring.homework1.domain;

import course.spring.homework1.dao.UsersRepository;
import course.spring.homework1.exception.NonexistingEntityException;
import course.spring.homework1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    UsersRepository userRepo;

    @Override
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User findUserByIs(String userId) {
        return userRepo.findById(userId).orElseThrow(() -> new NonexistingEntityException(String.format("There is no user with id '%s'",userId)));
    }

    @Override
    public User addUser(User user) {
        return userRepo.insert(user);
    }

    @Override
    public User updateUser(User user) {
        Optional<User> oldUser = userRepo.findById(user.getId());
        if(!oldUser.isPresent())
        {
            throw new NonexistingEntityException(String.format("There is no user with id '%s'",user.getId()));
        }
        return userRepo.save(user);
    }

    @Override
    public User removeUser(String userId) {
        Optional<User> oldUser = userRepo.findById(userId);
        if(!oldUser.isPresent())
        {
            throw new NonexistingEntityException(String.format("There is no user with id '%s'",userId));
        }
        userRepo.deleteById(userId);
        return oldUser.get();
    }
}

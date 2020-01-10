package course.spring.webmvc.domain;

import course.spring.webmvc.dao.UsersRepository;
import course.spring.webmvc.exception.NonexistingEntityException;
import course.spring.webmvc.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UsersServiceImpl implements UsersService {

    @Autowired
    UsersRepository userRepo;

    @Override
    @PostFilter("filterObject.id == authentication.principal.id or hasRole('ADMIN')")
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User findUserByIs(String userId) {
        return userRepo.findById(userId).orElseThrow(() -> new NonexistingEntityException(String.format("There is no user with id '%s'",userId)));
    }

    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow(() -> new NonexistingEntityException(
                String.format("User with username='%s' does not exist.", username)));
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new NonexistingEntityException(
                String.format("User with email='%s' does not exist.", email)));
    }

    @Override
    public User addUser(User user)
    {
        if(user.getRole() == null || user.getRole().trim().length() == 0) {
            user.setRole("ROLE_AUTHOR");
        }
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setStatus("active");
        return userRepo.insert(user);
    }

    @Override
    public User updateUser(User user) {
        Optional<User> oldUser = userRepo.findById(user.getId());
        if(!oldUser.isPresent())
        {
            throw new NonexistingEntityException(String.format("There is no user with id '%s'",user.getId()));
        }
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
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
    @Override
    public User activateUser(String userId){
        User user = findUserByIs(userId);
        user.setStatus("active");
        return updateUser(user);
    }

    @Override
    public User deactivateUser(String userId){
        User user = findUserByIs(userId);
        user.setStatus("inactive");
        return updateUser(user);
    }

    @Override
    public long count() {
        return userRepo.count();
    }
}

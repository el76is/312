package kata.academy.crud.service;

import kata.academy.crud.entities.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    void create(User user);

    void update(User user);

    void delete(Long id);
}
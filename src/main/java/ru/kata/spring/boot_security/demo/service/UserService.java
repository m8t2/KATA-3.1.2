package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {

    User findUser(Long id);

    void addUser(User user);

    List<User> findAllUsers();

    void updateUser(User user, List<Long> roleIds);
}

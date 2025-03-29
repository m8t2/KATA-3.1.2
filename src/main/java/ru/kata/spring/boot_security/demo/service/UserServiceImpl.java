package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.security.SecurityService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    @Transactional
    public void addUser(User user) {
        passwordEncoder.encode(user.getPassword());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(User user, List<Long> roleIds) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existingUser.setName(user.getName());
        existingUser.setSecondname(user.getSecondname());
        existingUser.setAge(user.getAge());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            passwordEncoder.encode(existingUser.getPassword());
        }

        Set<Role> updatedRoles = roleService.findRolesByIds(roleIds);

        existingUser.getRoles().clear();
        existingUser.getRoles().addAll(updatedRoles);

        userRepository.save(existingUser);
    }
}
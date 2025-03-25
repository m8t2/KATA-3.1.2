package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final UserServiceImpl userDetailsService;
    private final RoleServiceImpl roleServiceimpl;

    @Autowired
    public AdminController(UserRepository userRepository, UserServiceImpl userDetailsService, RoleServiceImpl roleServiceimpl) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.roleServiceimpl = roleServiceimpl;
    }

    @GetMapping("/admin")
    public String admin(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        List<User> users = userDetailsService.findAllUsers();
        model.addAttribute("usersDetails", userDetails);
        model.addAttribute("users", users);
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleServiceimpl.findAllRoles());
        return "admin";
    }

    @GetMapping("/admin/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/update")
    public String editUserForm(@RequestParam("id") Long id, Model model) {
        User user = userDetailsService.findUser(id);
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleServiceimpl.findAllRoles());
        model.addAttribute("selectedRoleIds", user.getRoles().stream().map(Role::getId).collect(Collectors.toList()));
        return "admin/update";
    }

    @PostMapping("/admin/update")
    public String updateUser(
            @ModelAttribute("user") @Valid User user,
            BindingResult result,
            @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleServiceimpl.findAllRoles());
            model.addAttribute("selectedRoleIds", roleIds != null ? roleIds : List.of());
            return "admin/update";
        }

        userDetailsService.updateUser(user, roleIds);
        return "redirect:/admin";
    }

    @PostMapping("/admin/add")
    public String addUser(
            @ModelAttribute("user") @Valid User user,
            BindingResult result,
            @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleServiceimpl.findAllRoles());
            return "admin";
        }
        roleServiceimpl.SetUserRoles(roleIds, user);

        userDetailsService.addUser(user);
        return "redirect:/admin";
    }

}

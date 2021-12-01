package kata.academy.crud.controllers;

import javassist.NotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import kata.academy.crud.entities.Role;
import kata.academy.crud.entities.User;
import kata.academy.crud.service.RoleService;
import kata.academy.crud.service.UserService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("login")
    public String login() {
        return "login";
    }

    @GetMapping("user")
    public String user(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        model.addAttribute("roles", user.getRoles());
        return "user";
    }

    @GetMapping("admin")
    public String read(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("listRoles", roleService.getAllRoles());
        return "admin";
    }

    @PostMapping("admin/create")
    public String create(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult,
                         @RequestParam("roleName") String[] role)
            throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return "redirect:/admin";
        }
        if (userService.getUserByUsername(user.getUsername()) != null) {
            return "redirect:/admin";
        }
        Set<Role> rs = new HashSet<>();
        for (String roles : role) {
            rs.add(roleService.getByName(roles));
        }
        user.setRoles(rs);
        userService.create(user);
        return "redirect:/admin";
    }

    @PutMapping("admin/{id}")
    public String update(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult,
                         @RequestParam("roleName") String[] role)
            throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return "redirect:/admin";
        }
        Set<Role> rs = new HashSet<>();
        for (String roles : role) {
            rs.add(roleService.getByName(roles));
        }
        user.setRoles(rs);
        userService.update(user);
        return "redirect:/admin";
    }

    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    private void getUserRoles(User user) {
        user.setRoles(user.getRoles().stream()
                .map(role -> roleService.getByName(role.getRole()))
                .collect(Collectors.toSet()));
    }
}

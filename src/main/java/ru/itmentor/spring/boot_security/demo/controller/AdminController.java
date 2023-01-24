package ru.itmentor.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.List;


@Controller
public class AdminController {
    private final UserService service;
    private boolean showtable = true;
    private boolean showedit = false;

    @Autowired
    public AdminController(UserService service) {
        this.service = service;
    }

    @RequestMapping("/")
    public String formroot() {
        return ("/login");
    }

    @RequestMapping("/login")
    public String formlogin() {
        return ("/login");
    }

    @RequestMapping("/admin")
    public String index(Model model) {
        model.addAttribute("list", service.getUsers());
        List<Role> roleList = service.getRoles();
        model.addAttribute("allRoles", roleList);

        if (model.containsAttribute("user") && ((User) model.getAttribute("user")).getId() == 0) showedit = false;
        if (model.containsAttribute("user") && ((User) model.getAttribute("user")).getId() != 0) showtable = true;

        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
            showtable = true;
            showedit = false;
        }
        model.addAttribute("Showtable", showtable);
        model.addAttribute("Showedit", showedit);
        return "admin";
    }

    @RequestMapping("/admin/new")
    public String newuser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("user", new User());
        showtable = false;
        return "redirect:/admin";
    }

    @RequestMapping(value = "admin/savenew", method = RequestMethod.POST)
    public String saveNewUser(@ModelAttribute("user") @Valid User user,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("bindingResult", bindingResult);
            showtable = false;
        } else {
        service.addUser(user);
            showtable = true;
        }
        return "redirect:/admin";
    }

    @RequestMapping(value = "admin/save", method = RequestMethod.POST)
    public String saveUser(@ModelAttribute("user") @Valid User user,
                           BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Role> roles = service.getRoles();
            model.addAttribute("allRoles", roles);
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("bindingResult", bindingResult);
            showedit = true;
            return "redirect:/admin";
        }
        service.updateUser(user.getId(), user);
        showedit = false;
        return "redirect:/admin";
    }

    @GetMapping("admin/edit/{id}")
    public String showUserProfileModal(@PathVariable("id") int id, Model model) {
        model.addAttribute("allRoles", service.getRoles());
        model.addAttribute("user", service.getUserById(id));
        return "fragments/edit_form";
    }

    @GetMapping("admin/pre_delete/{id}")
    public String showUserDelete(@PathVariable("id") int id, Model model) {
        model.addAttribute("allRoles", service.getRoles());
        model.addAttribute("user", service.getUserById(id));
        return "fragments/delete_form";
        }

    @RequestMapping("/admin/delete")
    public String delete(@ModelAttribute("user") User user, Model model) {
        service.deleteUser(user.getId());
        showtable = true;
        showedit = false;
        return "redirect:/admin";
    }
}

package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;
import sec.project.service.UserService;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;
    
    @Autowired
    private UserService userService;
    

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loadLogin() {
        return "login";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String address) {
        signupRepository.save(new Signup(name, address));
        return "done";
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginForm(@RequestParam String name, @RequestParam String password) {
        try {
            if (!userService.accounts().get(name).equals(password)) {
            return "redirect:/rekt";
            }
            userService.loadUserByUsername(name);
        } catch(Exception e) {
            return "redirect:/rekt";
        }
        return "done";
    }
    
    @RequestMapping(value = "/rekt", method = RequestMethod.GET)
    public String loadRekt() {
        return "rekt";
    }
    
    @RequestMapping(value = "/admin/info", method = RequestMethod.GET)
    public String loadInfo(Model model) {
        model.addAttribute("signups", signupRepository.findAll());
        model.addAttribute("users", userService.accounts());
        return "users";
    }
    
    @RequestMapping(value = "/users/{username}", method = RequestMethod.GET)
    public String loadUser(Model model, @PathVariable String username) {
        try {
            model.addAttribute("user", userService.loadUserByUsername(username));
        } catch(Exception e) {
            return "redirect:/rekt";
        }
        return "user";
    }
    
    @RequestMapping(value = "/delete/{username}", method = RequestMethod.POST)
    public String deleteUser(@RequestParam String name, @PathVariable String username) {
        final String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.remove(name);
        if (name != currentUserName) {
            System.out.println("you deleted the wrong account, dumbass");
        }
        return "done";
    }
    
}

package OneWayDev.tn.OneWayDev.Controller;

import OneWayDev.tn.OneWayDev.Entity.User;
import OneWayDev.tn.OneWayDev.Service.AuthService;
import OneWayDev.tn.OneWayDev.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("user/")
@CrossOrigin("*")
@Validated
public class UserController {

    private UserService userService;

    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable(value = "email") String email){
        return userService.getUserByEmail(email);
    }
}

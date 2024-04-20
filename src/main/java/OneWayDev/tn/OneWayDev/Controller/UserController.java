package OneWayDev.tn.OneWayDev.Controller;

import OneWayDev.tn.OneWayDev.Entity.User;
import OneWayDev.tn.OneWayDev.Service.AuthService;
import OneWayDev.tn.OneWayDev.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("user/")
@CrossOrigin("*")
@Validated
public class UserController {

    private UserService userService;

   @GetMapping("/email/{email}")
public ResponseEntity<Map<String, String>> getUserByEmail(@PathVariable(value = "email") String email){
    Map<String, String> response = new HashMap<>();
    try {
        User user = userService.getUserByEmail(email);
        response.put("status", "success");
        response.put("message", "User found");
        response.put("user", user.toString()); // assuming User class has a proper toString method
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        response.put("status", "error");
        response.put("message", "User not found with email: " + email);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}


    @GetMapping("/all")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @PutMapping("/block/{id}")
    public User blockUser(@PathVariable(value = "id") Long idUser){
        return userService.blockedUser(idUser, false);
    }

    @PutMapping("/unblock/{id}")
    public User unblockUser(@PathVariable(value = "id") Long idUser){
        return userService.blockedUser(idUser, true);
    }
}

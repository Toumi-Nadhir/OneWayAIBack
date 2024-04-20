package OneWayDev.tn.OneWayDev.Controller;

import OneWayDev.tn.OneWayDev.Entity.User;
import OneWayDev.tn.OneWayDev.Repository.UserRepository;
import OneWayDev.tn.OneWayDev.Service.AuthService;
import OneWayDev.tn.OneWayDev.Service.UserService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
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
    private UserRepository userRepository;

    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable(value = "email") String email){
        return userService.getUserByEmail(email);
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

    @PostMapping("/upload-profile-picture")
    public ResponseEntity<Map<String, String>> uploadProfilePicture(@RequestParam("file") MultipartFile file, @RequestParam("email") String email) {
        String filePath = userService.uploadFile(file);
        User user = userService.getUserByEmail(email);
        user.setPhotoProfile(filePath); // assuming User class has a setPhotoProfile method
        userRepository.save(user); // assuming UserService class has a save method

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Profile picture updated successfully");
        responseBody.put("filePath", filePath);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


 @GetMapping("/image/{imageName}")
public ResponseEntity<org.springframework.core.io.Resource> getImage(@PathVariable String imageName) {
    // Assuming images are stored in src/main/resources/uploads directory
    Path imagePath = Paths.get("src/main/resources/uploads").resolve(imageName);
    try {
        org.springframework.core.io.Resource resource = new UrlResource(imagePath.toUri());
        if (resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok().body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    } catch (Exception e) {
        return ResponseEntity.notFound().build();
    }
}
}

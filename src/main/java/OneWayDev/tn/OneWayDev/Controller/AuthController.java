package OneWayDev.tn.OneWayDev.Controller;

import OneWayDev.tn.OneWayDev.Entity.User;
import OneWayDev.tn.OneWayDev.Repository.UserRepository;
import OneWayDev.tn.OneWayDev.Service.AuthService;
import OneWayDev.tn.OneWayDev.Service.MailConfirmationService;
import OneWayDev.tn.OneWayDev.Service.UserService;
import OneWayDev.tn.OneWayDev.dto.request.AuthenticationRequest;
import OneWayDev.tn.OneWayDev.dto.request.CustomErrorResponse;
import OneWayDev.tn.OneWayDev.dto.request.RegisterRequest;
import OneWayDev.tn.OneWayDev.exception.EmailExistsExecption;
import OneWayDev.tn.OneWayDev.exception.InvalidPasswordException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("auth")
@CrossOrigin("*")
@Validated
public class AuthController {

    private AuthService authenticationService;
    private final MailConfirmationService mailConfirmationService;
    private UserService userService;
    private UserRepository userRepository;
    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@RequestBody @Valid RegisterRequest registerRequestDTO){
        try {
            return new ResponseEntity<>(authenticationService.register(registerRequestDTO), HttpStatus.CREATED);
        }
        catch (EmailExistsExecption e){
            return new ResponseEntity<>(new CustomErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage()), HttpStatus.CONFLICT);
        } catch (Exception e) {
            System.out.println(e.getClass().getName());
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping( "/confirm")
    public String confirm(@RequestParam("token") String token) {
        return mailConfirmationService.confirmToken(token);
    }


    @PostMapping("/login")
    public ResponseEntity<?> jwtToken( @RequestBody @Valid AuthenticationRequest loginRequest){
        try{
            return new ResponseEntity<>(authenticationService.jwtToken(loginRequest.getUsername(), loginRequest.getPassword()), HttpStatus.OK);
        }catch (BadCredentialsException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }catch (LockedException e){
            return new ResponseEntity<>("User account is locked", HttpStatus.LOCKED);
        }catch (DisabledException e) {
            return new ResponseEntity<>("User is disabled. Please verify your email to activate your account.", HttpStatus.UNAUTHORIZED);
        }catch (Exception e) {
            System.out.println("Caught Exception: " + e.getClass().getName());
            System.out.println("Exception Message: " + e.getMessage());
            return new ResponseEntity<>("An unexpected error occurred try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

   /* @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        emailService.sendResetTokenEmail(token, user);

        return ResponseEntity.ok().body("Reset password email sent");
    }*/
    @GetMapping("/loginn")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public String getUser(){
        return "hello admin";
    }


    // In AuthController.java
@PostMapping("/forgot-password")
public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
    Map<String, String> response = new HashMap<>();
    try {
        String email = request.get("email");
        authenticationService.forgotPassword(email);
        response.put("message", "Reset password email sent");
        return ResponseEntity.ok().body(response);
    } catch (UsernameNotFoundException e) {
        response.put("error", "User not found with email: " + request.get("email"));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (Exception e) {
        response.put("error", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

@PostMapping("/verify-code")
public ResponseEntity<Map<String, String>> verifyCode(@RequestBody Map<String, String> request) {
    Map<String, String> response = new HashMap<>();
    try {
        String code = request.get("code");
        authenticationService.verifyCode(code);
        response.put("message", "Code verified");
        return ResponseEntity.ok().body(response);
    } catch (Exception e) {
        response.put("error", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

 @PostMapping("/reset-password")
public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
    Map<String, String> response = new HashMap<>();
    try {
        String token = request.get("token");
        String password = request.get("password");
        String confirmPassword = request.get("confirmPassword");

        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
        if (!password.matches(pattern)) {
            response.put("error", "Password must contain at least one lowercase letter, one uppercase letter, and one number.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        authenticationService.resetPassword(token, password, confirmPassword);
        response.put("message", "Password reset successfully");
        return ResponseEntity.ok().body(response);
    } catch (Exception e) {
        response.put("error", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
}

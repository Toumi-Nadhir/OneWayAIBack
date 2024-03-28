package OneWayDev.tn.OneWayDev.Controller;

import OneWayDev.tn.OneWayDev.Entity.User;
import OneWayDev.tn.OneWayDev.Service.AuthService;
import OneWayDev.tn.OneWayDev.Service.MailConfirmationService;
import OneWayDev.tn.OneWayDev.dto.request.AuthenticationRequest;
import OneWayDev.tn.OneWayDev.dto.request.RegisterRequest;
import OneWayDev.tn.OneWayDev.exception.EmailExistsExecption;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("auth")
@CrossOrigin("*")
@Validated
public class AuthController {

    private AuthService authenticationService;
    private final MailConfirmationService mailConfirmationService;
    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@RequestBody @Valid RegisterRequest registerRequestDTO){
        try {
            return new ResponseEntity<>(authenticationService.register(registerRequestDTO), HttpStatus.CREATED);
        }
        catch (EmailExistsExecption e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            System.out.println(e.getClass().getName());
            System.out.println(e.getMessage());
            return new ResponseEntity<>("An unexpected error occurred try again", HttpStatus.INTERNAL_SERVER_ERROR);
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
    @GetMapping("/loginn")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public String getUser(){
        return "hello admin";
    }
}

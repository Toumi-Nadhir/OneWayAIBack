package OneWayDev.tn.OneWayDev.Service;

import OneWayDev.tn.OneWayDev.Entity.MailToken;
import OneWayDev.tn.OneWayDev.Entity.Role;
import OneWayDev.tn.OneWayDev.Entity.User;
import OneWayDev.tn.OneWayDev.Enum.RoleType;
import OneWayDev.tn.OneWayDev.Repository.RoleRepository;
import OneWayDev.tn.OneWayDev.Repository.UserRepository;
import OneWayDev.tn.OneWayDev.dto.request.RegisterRequest;
import OneWayDev.tn.OneWayDev.exception.EmailExistsExecption;
import OneWayDev.tn.OneWayDev.exception.MobileNumberExistsExecption;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import okhttp3.*;
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    private final EmailService emailService;
    private final MailConfirmationService mailConfirmationService;
    private  final MailTokenService mailTokenService;

    @Value("${infobip.base-url}")
    private String INFOBIP_BASE_URL ;

    @Value("${infobip.api-key}")
    private String INFOBIP_API_KEY ;

    @Value("${infobip.sender-name}")
    private String INFOBIP_SENDER_NAME ;
    public User register(RegisterRequest registerRequestDTO) {
        try{
            if(userRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()){
                throw new EmailExistsExecption("Email already exists");
            }
            Role role=roleRepository.findByRoleType(RoleType.USER).get();
            User user=new User();
            user.setFirstName(registerRequestDTO.getFirstName());
            user.setLastName(registerRequestDTO.getLastName());
            user.setPhone(registerRequestDTO.getMobileNumber());
            user.setPassword(bCryptPasswordEncoder.encode(registerRequestDTO.getPassword()));
            user.setEmail(registerRequestDTO.getEmail());
            user.setEnabled(false);
            user.setNonLocked(true);

            user.setRoles(List.of(role));
            String token = UUID.randomUUID().toString();
            MailToken confirmationToken = new MailToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    user
            );

            mailTokenService.saveConfirmationToken(
                    confirmationToken);
            String link = "http://localhost:1919/auth/confirm?token=" + token;
            emailService.sendEmail(
                    registerRequestDTO.getEmail(),
                    mailConfirmationService.buildEmail(registerRequestDTO.getFirstName(), link));
          //  String photoName= uploadFile(registerRequestDTO.getPhotoProfile());
            //user.setPhotoProfile(photoName);
            return userRepository.save(user);
        }catch (MailSendException mailSendException){
            throw new MailSendException("Sorry, we couldn't send your email at the moment. Please try again later ");
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }

    public Map<String, String> jwtToken(String username, String password)
    {

        String subject=null;
        String scope=null;
        String  grantType="password";
        Boolean withRefreshToken=true;
        String refreshToken=null;
        if(grantType.equals("password")){

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            subject=authentication.getName();

            System.out.println("username "+subject);
            scope=authentication.getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

        } else if(grantType.equals("refreshToken")){
            if(refreshToken==null) {
                return Map.of("errorMessage","Refresh  Token is required");
            }
            Jwt decodeJWT = null;
            try {
                decodeJWT = jwtDecoder.decode(refreshToken);
            } catch (JwtException e) {
                return Map.of("errorMessage",e.getMessage());
            }
            subject=decodeJWT.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            scope=authorities.stream().map(auth->auth.getAuthority()).collect(Collectors.joining(" "));
        }
        Map<String, String> idToken=new HashMap<>();
        Instant instant=Instant.now();
        JwtClaimsSet jwtClaimsSet=JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(instant)
                .expiresAt(instant.plus(withRefreshToken?15:20, ChronoUnit.MINUTES))
                .issuer("security-service")
                .claim("scope",scope)
                .build();
        String jwtAccessToken=jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        idToken.put("accessToken",jwtAccessToken);
        if(withRefreshToken){
            JwtClaimsSet jwtClaimsSetRefresh=JwtClaimsSet.builder()
                    .subject(subject)
                    .issuedAt(instant)
                    .expiresAt(instant.plus(20, ChronoUnit.MINUTES))
                    .issuer("security-service")
                    .build();
            String jwtRefreshToken=jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSetRefresh)).getTokenValue();
            idToken.put("refreshToken",jwtRefreshToken);
        }
        return idToken;
    }



    public void sendSms(String to, String body, String senderName) {
        try {
            OkHttpClient client = new OkHttpClient();


            String jsonBody = String.format(
                    "{\"messages\":[{\"destinations\":[{\"to\":\"%s\"}],\"from\":\"%s\",\"text\":\"%s\"}]}",
                    to, senderName, body
            );
            RequestBody requestBody = RequestBody.create(jsonBody, okhttp3.MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(INFOBIP_BASE_URL + "/sms/2/text/advanced")
                    .method("POST", requestBody)
                    .addHeader("Authorization", "App " + INFOBIP_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // In AuthService.java
    public void forgotPassword(String email) {
        // Find the user by email
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new UsernameNotFoundException("No user found with email: " + email);
        }
        User user = optionalUser.get();

        // Generate a unique token
        String token = String.format("%04d", new Random().nextInt(10000));

        // Create a MailToken entity and save it in the database
        MailToken mailToken = new MailToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        mailTokenService.saveConfirmationToken(mailToken);

        // Send the code to the user's email
        emailService.sendEmail(
                email,
                "Your verification code is: " + token
        );
    }

    public void verifyCode(String code) {
        // Find the MailToken by token
        Optional<MailToken> optionalMailToken = mailTokenService.getToken(code);
        if (!optionalMailToken.isPresent()) {
            throw new IllegalArgumentException("Invalid code");
        }

        // Check if the code is expired
        MailToken mailToken = optionalMailToken.get();
        if (mailToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Code expired");
        }
    }

    public void resetPassword(String token, String password, String confirmPassword) {
        // Check if the password and confirmPassword are the same
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password and confirm password do not match");
        }

        // Find the user by token
        Optional<MailToken> optionalMailToken = mailTokenService.getToken(token);
        if (!optionalMailToken.isPresent()) {
            throw new IllegalArgumentException("Invalid code");
        }
        User user = optionalMailToken.get().getUser();

        // Update the user's password
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }
}

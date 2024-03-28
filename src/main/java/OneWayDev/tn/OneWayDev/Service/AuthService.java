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

    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(" Photo profile is required ,should select a photo");
        }
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (filename.contains("..")) {
                throw new IllegalArgumentException("Cannot upload file with relative path outside current directory");
            }
            Path uploadDir = Paths.get("src/main/resources/upload");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            return filename;

        } catch (Exception e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }

    public String forgotPassword(String mobileNumber) {
        Optional<User> checkUser = userRepository.findByPhone(mobileNumber);
        if (!checkUser.isPresent()) {
            throw new MobileNumberExistsExecption("No user found with this mobile phone number");
        }
        User user = checkUser.get();
        UUID randomUUID = UUID.randomUUID();
        String code = randomUUID.toString().substring(0, 8);
        String sms="Your verification code is: "+code ;
        sendSms(mobileNumber, sms, INFOBIP_SENDER_NAME);
        user.setPassword(bCryptPasswordEncoder.encode(code));
        userRepository.save(user);
        return code;
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

}

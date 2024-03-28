package OneWayDev.tn.OneWayDev.dto.request;


import OneWayDev.tn.OneWayDev.Enum.RoleType;
import OneWayDev.tn.OneWayDev.advice.ValidRoleType;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data @AllArgsConstructor @NoArgsConstructor
public class RegisterRequest {
    @Valid


    @NotBlank(message = "first name  is required and cannot be blank.")
    @Size(min=3,max = 25,message = "first name length min is 3 and max is 25")
    private String firstName;
    @NotBlank(message = "last name is required and cannot be blank.")
    @Size(min=3,max = 25,message = "last name length min is 3 and max is 25")
    private String lastName;
    @NotBlank(message = "password is required and cannot be blank.")
    @Size(min = 8,max = 22,message = "password should be min 8 caracters and 22 caracters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one number.")
    private String password;
    @NotBlank(message = "phone number is required and cannot be blank.")
    @Pattern(regexp = "\\d{8}", message = "Invalid phone number format. It should be 8 numbers.")
    private String mobileNumber;
    @Email(message = "inavalid mail format")
    @NotBlank(message = "email is required and cannot be blank.")
    @Column(unique = true)
    private String email;
//    @ValidRoleType
//    private RoleType role;
//    @NotNull(message = "Photo profile is required.")
//    private MultipartFile photoProfile;


}

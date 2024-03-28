package OneWayDev.tn.OneWayDev;

import OneWayDev.tn.OneWayDev.Entity.Role;
import OneWayDev.tn.OneWayDev.Entity.User;
import OneWayDev.tn.OneWayDev.Enum.RoleType;
import OneWayDev.tn.OneWayDev.Repository.RoleRepository;
import OneWayDev.tn.OneWayDev.Repository.UserRepository;
import OneWayDev.tn.OneWayDev.config.RsakeysConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
@EnableConfigurationProperties(RsakeysConfig.class)
@RequiredArgsConstructor
public class OneWayDevApplication implements CommandLineRunner {
	private  final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	public static void main(String[] args) {
		SpringApplication.run(OneWayDevApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
       /* Stream.of(RoleType.ADMIN, RoleType.USER)
            .forEach(roleType -> {
               Role role= new Role();
               role.setRoleType(roleType);
               roleRepository.save(role);
            });
       User admin=new User();
        Role role=roleRepository.findByRoleType(RoleType.ADMIN).get();
        admin.setFirstName("admin");
        admin.setLastName("admin");
        admin.setEmail("admin@1waydev.tn");
        admin.setPhone("28598395");
        admin.setRoles(List.of(role));
        admin.setEnabled(true);
        admin.setNonLocked(true);
        admin.setPassword(passwordEncoder.encode("adminADMIN#1919"));
       userRepository.save(admin);
	*/}
}

package OneWayDev.tn.OneWayDev.Service;

import OneWayDev.tn.OneWayDev.Entity.User;
import OneWayDev.tn.OneWayDev.Enum.RoleType;
import OneWayDev.tn.OneWayDev.Repository.UserRepository;
import OneWayDev.tn.OneWayDev.dto.request.ProfileRequest;
import OneWayDev.tn.OneWayDev.exception.NotFoundExecption;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email " + email));
    }

  /*  public Page<User>findUserswithPaginationAndSorting(int offset, int pageSize, String field, String roleType){
        Page<User> users = userRepository.findByRolesRoleType(RoleType.valueOf(roleType), PageRequest.of(offset, pageSize, Sort.by(field)));
        return users;
    }*/

    public User findUserById(Long idUser){
       Optional<User>  user= userRepository.findById(idUser);
       if (!user.isPresent()){
           throw new NotFoundExecption("no user found");
       }
       return user.get();
    }

    public User manageProfile(Long idUser, ProfileRequest profile){
        Optional<User>  findUser= userRepository.findById(idUser);
        if (!findUser.isPresent()){
            throw new NotFoundExecption("no user found");
        }
        User user= findUser.get();
        user.setFirstName(profile.getFirstName());
        user.setLastName(profile.getLastName());
        user.setPhone(profile.getMobileNumber());
        return userRepository.save(user);
    }

    public User enabledUser(Long idUser, Boolean enable){
        Optional<User>  findUser= userRepository.findById(idUser);
        if (!findUser.isPresent()){
            throw new NotFoundExecption("no user found");
        }
        User user= findUser.get();
        user.setEnabled(enable);
        return userRepository.save(user);
    }
    public User blockedUser(Long idUser, Boolean blocked){
        Optional<User>  findUser= userRepository.findById(idUser);
        if (!findUser.isPresent()){
            throw new NotFoundExecption("no user found");
        }

        User user= findUser.get();
        user.setNonLocked(blocked);
        return userRepository.save(user);
    }

    public int enableAppUser(String email) {
        return userRepository.enableAppUser(email);
    }


    public List<User> getAllUsers() {
        return userRepository.findByRolesRoleType(RoleType.USER);
    }
}

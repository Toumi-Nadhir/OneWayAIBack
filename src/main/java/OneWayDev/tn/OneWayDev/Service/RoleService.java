package OneWayDev.tn.OneWayDev.Service;

import OneWayDev.tn.OneWayDev.Entity.Role;
import OneWayDev.tn.OneWayDev.Repository.RoleRepository;
import OneWayDev.tn.OneWayDev.exception.RoleExistsExecption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    public Role addRole( Role role){
        Optional<Role> findrole= roleRepository.findByRoleType(role.getRoleType());
        if (findrole.isPresent()){
            throw new RoleExistsExecption("role is exist");
        }

        return roleRepository.save(role);
    }
    public List<Role> findAll(){
        List<Role>  roles= roleRepository.findAll();
        return roles;
    }

}

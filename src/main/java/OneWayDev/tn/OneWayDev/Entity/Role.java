package OneWayDev.tn.OneWayDev.Entity;

import OneWayDev.tn.OneWayDev.Enum.RoleType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RoleType roleType;
    @ManyToMany(mappedBy = "roles")
    private List<User> users;

}

package OneWayDev.tn.OneWayDev.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class ModelSTT {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String url;

    @Lob
    @Column(columnDefinition="MEDIUMTEXT")
    private String image;

    @ManyToMany(mappedBy = "modelSTTs")
    private List<User> users;
}
package OneWayDev.tn.OneWayDev.Entity;

 import jakarta.persistence.*;
 import lombok.Getter;
 import lombok.Setter;

 import java.io.Serializable;

@Entity
@Getter
@Setter
public class Audio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @Lob
    @Column(columnDefinition="MEDIUMBLOB")
    private byte[] audioFile;

    @Lob
    @Column(columnDefinition="MEDIUMTEXT")
    private String transcription;



    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}

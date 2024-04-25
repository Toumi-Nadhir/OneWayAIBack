package OneWayDev.tn.OneWayDev.Repository;
import OneWayDev.tn.OneWayDev.Entity.Audio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AudioRepository extends JpaRepository<Audio, Long> {
    Optional<Audio> findByFilename(String filename);
}
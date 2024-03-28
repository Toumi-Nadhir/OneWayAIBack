package OneWayDev.tn.OneWayDev.Repository;


import OneWayDev.tn.OneWayDev.Entity.MailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MailTokenRepository extends JpaRepository<MailToken,Long> {
    Optional<MailToken>findByToken(String token);
    @Transactional
    @Modifying
    @Query("UPDATE MailToken m " +
            "SET m.confirmedAt = ?2 " +
            "WHERE m.token = ?1")
    int updateConfirmedAt(String token,
                          LocalDateTime confirmedAt);

}

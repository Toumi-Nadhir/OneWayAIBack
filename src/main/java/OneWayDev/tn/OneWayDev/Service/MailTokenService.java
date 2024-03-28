package OneWayDev.tn.OneWayDev.Service;

import OneWayDev.tn.OneWayDev.Entity.MailToken;
import OneWayDev.tn.OneWayDev.Repository.MailTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailTokenService {


        private final MailTokenRepository mailTokenRepository;

        public void saveConfirmationToken(MailToken token) {
            mailTokenRepository.save(token);
        }

        public Optional<MailToken> getToken(String token) {
            return mailTokenRepository.findByToken(token);
        }

        public int setConfirmedAt(String token) {
            return mailTokenRepository.updateConfirmedAt(
                    token, LocalDateTime.now());
        }

    }

package hyperskill.antifraud.service;

import hyperskill.antifraud.dto.DeleteStatusDTO;
import hyperskill.antifraud.model.database.StolenCardEntity;
import hyperskill.antifraud.model.database.SuspiciousIpEntity;
import hyperskill.antifraud.repository.StolenCardRepository;
import hyperskill.antifraud.repository.SuspiciousIpRepository;
import hyperskill.antifraud.service.utils.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityService {
    private final StolenCardRepository stolenCardRepository;
    private final SuspiciousIpRepository suspiciousIpRepository;
    public SecurityService(StolenCardRepository stolenCardRepository, SuspiciousIpRepository suspiciousIpRepository) {
        this.stolenCardRepository = stolenCardRepository;
        this.suspiciousIpRepository = suspiciousIpRepository;
    }

    public ResponseEntity<SuspiciousIpEntity> addSuspiciousIp(String ip) {
        if (suspiciousIpRepository.existsByIp(ip)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (Validator.isIpInvalid(ip)) {
            return ResponseEntity.badRequest().build();
        }
        SuspiciousIpEntity suspiciousIpEntity = new SuspiciousIpEntity(ip);
        suspiciousIpRepository.save(suspiciousIpEntity);
        return ResponseEntity.ok().body(suspiciousIpEntity);
    }

    public ResponseEntity<DeleteStatusDTO> deleteSuspiciousIp(String ip) {
        Long deleteCount = suspiciousIpRepository.deleteByIp(ip);
        if (Validator.isIpInvalid(ip)) {
            return ResponseEntity.badRequest().build();
        }
        if (deleteCount < 1) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(
                new DeleteStatusDTO(String.format("IP %s successfully removed!", ip)));
    }

    public ResponseEntity<List<SuspiciousIpEntity>> getSuspiciousIps() {
        return ResponseEntity.ok().body(suspiciousIpRepository.findAll());
    }

    public ResponseEntity<StolenCardEntity> addStolenCard(String number) {
        if (stolenCardRepository.existsByNumber(number)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (Validator.isCardNumberInvalid(number)) {
            return ResponseEntity.badRequest().build();
        }
        StolenCardEntity stolenCard = new StolenCardEntity(number);
        stolenCardRepository.save(stolenCard);
        return ResponseEntity.ok().body(stolenCard);
    }

    public ResponseEntity<DeleteStatusDTO> deleteStolenCard(String number) {
        Long deleteCount = stolenCardRepository.deleteByNumber(number);
        if (Validator.isCardNumberInvalid(number)) {
            return ResponseEntity.badRequest().build();
        }
        if (deleteCount < 1 ) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(new DeleteStatusDTO(String.format("Card %s successfully removed!", number)));
    }

    public ResponseEntity<List<StolenCardEntity>> getStolenCards() {
        return ResponseEntity.ok().body(stolenCardRepository.findAll());
    }
}

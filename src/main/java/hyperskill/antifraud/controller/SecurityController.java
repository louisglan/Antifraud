package hyperskill.antifraud.controller;

import hyperskill.antifraud.dto.DeleteStatusDTO;
import hyperskill.antifraud.dto.StolenCardDTO;
import hyperskill.antifraud.dto.SuspiciousIpDTO;
import hyperskill.antifraud.model.database.StolenCardEntity;
import hyperskill.antifraud.model.database.SuspiciousIpEntity;
import hyperskill.antifraud.service.SecurityService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class SecurityController {
    private final SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }
    @PostMapping(path = "/suspicious-ip")
    public ResponseEntity<?> addSuspicousIp(@RequestBody SuspiciousIpDTO suspiciousIpDTO) {
        return securityService.addSuspiciousIp(suspiciousIpDTO.ip()) ;
    }

    @Transactional
    @DeleteMapping(path = "/suspicious-ip/{ip}")
    public ResponseEntity<DeleteStatusDTO> deleteSuspiciousIp(@PathVariable String ip) {
        return securityService.deleteSuspiciousIp(ip);
    }

    @GetMapping(path = "/suspicious-ip")
    public ResponseEntity<List<SuspiciousIpEntity>> getSuspiciousIps() {
        return securityService.getSuspiciousIps();
    }

    @PostMapping(path = "/stolencard")
    public ResponseEntity<StolenCardEntity> addStolenCard(@RequestBody StolenCardDTO stolenCard) {
        return securityService.addStolenCard(stolenCard.number());
    }

    @Transactional
    @DeleteMapping(path = "/stolencard/{number}")
    public ResponseEntity<DeleteStatusDTO> removeStolenCard(@PathVariable String number) {
        return securityService.deleteStolenCard(number);
    }

    @GetMapping(path = "/stolencard")
    public ResponseEntity<List<StolenCardEntity>> getAllStolenCards() {
        return securityService.getStolenCards();
    }
}

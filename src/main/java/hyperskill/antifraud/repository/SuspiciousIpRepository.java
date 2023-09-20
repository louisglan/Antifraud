package hyperskill.antifraud.repository;

import hyperskill.antifraud.model.database.SuspiciousIpEntity;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SuspiciousIpRepository extends CrudRepository<SuspiciousIpEntity, Integer> {
    boolean existsByIp(String ip);

    Long deleteByIp(String ip);

    @NonNull
    List<SuspiciousIpEntity> findAll();

    Optional<SuspiciousIpEntity> findByIp(String ip);
}

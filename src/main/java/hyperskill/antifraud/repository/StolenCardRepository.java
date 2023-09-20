package hyperskill.antifraud.repository;

import hyperskill.antifraud.model.database.StolenCardEntity;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StolenCardRepository extends CrudRepository<StolenCardEntity, Integer> {
    boolean existsByNumber(String number);

    Long deleteByNumber(String number);

    @NonNull
    List<StolenCardEntity> findAll();

    Optional<StolenCardEntity> findByNumber(String number);
}

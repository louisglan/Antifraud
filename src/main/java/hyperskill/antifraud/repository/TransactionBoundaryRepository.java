package hyperskill.antifraud.repository;

import hyperskill.antifraud.model.database.TransactionBoundaryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TransactionBoundaryRepository extends CrudRepository<TransactionBoundaryEntity, Integer> {
    Optional<TransactionBoundaryEntity> findByNumber(String number);
}

package hyperskill.antifraud.repository;

import hyperskill.antifraud.model.database.TransactionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends CrudRepository<TransactionEntity, Integer> {
    @Query("SELECT region FROM TransactionEntity t WHERE t.number = :number AND t.date BETWEEN :startDate AND :endDate GROUP BY t.region")
    List<String> findByNumberWithinOneHourOfTransactionGroupedByRegion(
            @Param("number") String number, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    @Query("SELECT ip FROM TransactionEntity t WHERE t.number = :number AND t.date BETWEEN :startDate AND :endDate GROUP BY t.ip")
    List<String> findByNumberWithinOneHourOfTransactionGroupedByIp(
            @Param("number") String number, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Optional<TransactionEntity> findByTransactionId(Long id);

    List<TransactionEntity> findByOrderByTransactionId();

    List<TransactionEntity> findByNumber(String number);
}

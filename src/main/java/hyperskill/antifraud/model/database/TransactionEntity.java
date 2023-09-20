package hyperskill.antifraud.model.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class TransactionEntity {

    @Id
    @GeneratedValue
    private Long transactionId;

    private Long amount;

    @NonNull
    private String ip;

    @NonNull
    private String number;

    @NonNull
    private String region;

    private LocalDateTime date;
    private String result;
    private String feedback;
}

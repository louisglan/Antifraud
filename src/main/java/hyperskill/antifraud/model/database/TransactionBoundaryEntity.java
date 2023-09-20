package hyperskill.antifraud.model.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class TransactionBoundaryEntity {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String number;

    @NonNull
    private Long maxAllowed;

    @NonNull
    private Long maxManual;
}

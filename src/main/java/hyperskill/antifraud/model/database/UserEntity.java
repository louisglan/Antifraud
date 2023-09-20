package hyperskill.antifraud.model.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class UserEntity {
    private final String ROLE_ = "ROLE_";

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String name;

    @Column(unique=true)
    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    private String role;

    @NonNull
    private Boolean isAccountNonLocked;

    public void setRole(String role) {
        this.role = ROLE_ + role;
    }
}

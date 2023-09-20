package hyperskill.antifraud.repository;

import hyperskill.antifraud.model.database.UserEntity;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findUserEntityByUsername(String username);

    long deleteByUsername(String username);

    @NonNull
    List<UserEntity> findAll();
}

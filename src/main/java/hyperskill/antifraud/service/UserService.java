package hyperskill.antifraud.service;

import hyperskill.antifraud.dto.*;
import hyperskill.antifraud.model.database.UserEntity;
import hyperskill.antifraud.repository.UserRepository;
import hyperskill.antifraud.service.enums.ActivationStatus;
import hyperskill.antifraud.service.enums.ActivationStatusChangeOperation;
import hyperskill.antifraud.service.enums.Role;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final String ROLE_ = "ROLE_";

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, ModelMapper modelMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }
    public ResponseEntity<UserDTO> saveUser(RegistrationDTO body) {
        if (Objects.isNull(body.name()) || Objects.isNull(body.username()) || Objects.isNull(body.password())) {
            return ResponseEntity.badRequest().build();
        }
        UserEntity userEntity = new UserEntity(
                body.name(),
                body.username().toLowerCase(),
                passwordEncoder.encode(body.password()),
                ROLE_ + Role.MERCHANT.getRole(),
                false);
        if (userRepository.findAll().isEmpty()) {
            userEntity.setRole(Role.ADMINISTRATOR.getRole());
            userEntity.setIsAccountNonLocked(true);
        }
        try {
            userRepository.save(userEntity);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        UserDTO response = convertUserEntityToUserDTO(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<UserDTO> responseBodies = userEntities.stream().
                sorted(Comparator.comparingLong(UserEntity::getId))
                .map(this::convertUserEntityToUserDTO).toList();
        return ResponseEntity.ok().body(responseBodies);
    }

    public ResponseEntity<DeleteUserDTO> deleteUser(String username) {
            if (userRepository.deleteByUsername(username) < 1) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(new DeleteUserDTO(username, "Deleted successfully!"));
    }

    public ResponseEntity<UserDTO> changeRole(ChangeRoleDTO body) {
        Optional<UserEntity> userResponse = userRepository.findUserEntityByUsername(body.username());
        if (userResponse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        UserEntity userEntity = userResponse.get();
        if (!body.role().equals(Role.SUPPORT.getRole())
                && !body.role().equals(Role.MERCHANT.getRole())) {
            return ResponseEntity.badRequest().build();
        }
        if (userEntity.getRole().equals(ROLE_ + body.role())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        userEntity.setRole(body.role());
        userRepository.save(userEntity);
        UserDTO response = convertUserEntityToUserDTO(userEntity);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<LockedStatusDTO> changeLockedStatus(ChangeLockStatusDTO body) {
        Optional<UserEntity> userResponse = userRepository.findUserEntityByUsername(body.username());
        if (userResponse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        UserEntity userEntity = userResponse.get();
        if (userEntity.getRole().equals(Role.ADMINISTRATOR.getRole())) {
            return ResponseEntity.badRequest().build();
        }
        boolean isAccountNonLocked = body.operation().equals(ActivationStatusChangeOperation.UNLOCK.getOperation());

        String activationStatus =
                isAccountNonLocked ?
                        ActivationStatus.UNLOCKED.getActivationStatus() :
                        ActivationStatus.LOCKED.getActivationStatus();
        userEntity.setIsAccountNonLocked(isAccountNonLocked);
        userRepository.save(userEntity);
        return ResponseEntity.ok().body(
                new LockedStatusDTO(String.format("User %s %s!", body.username(), activationStatus)));
    }

    private UserDTO convertUserEntityToUserDTO(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDTO.class);
    }
}

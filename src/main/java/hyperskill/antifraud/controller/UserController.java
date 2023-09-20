package hyperskill.antifraud.controller;

import hyperskill.antifraud.dto.*;
import hyperskill.antifraud.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/user")
    public ResponseEntity<UserDTO> register(@RequestBody RegistrationDTO body) {
        return userService.saveUser(body);
    }

    @GetMapping(path = "/list")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return userService.getAllUsers();
    }

    @Transactional
    @DeleteMapping(path = "/user/{username}")
    public ResponseEntity<DeleteUserDTO> deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }

    @PutMapping(path = "/role")
    public ResponseEntity<UserDTO> changeRole(@RequestBody ChangeRoleDTO body) {
        return userService.changeRole(body);
    }

    @PutMapping(path = "/access")
    public ResponseEntity<LockedStatusDTO> changeLockedStatus(@RequestBody ChangeLockStatusDTO body) {
        return userService.changeLockedStatus(body);
    }
}

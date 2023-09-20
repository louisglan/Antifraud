package hyperskill.antifraud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDTO {
    private Long id;
    private String name;
    private String username;
    private String role;

    public String getRole() {
        return role.substring(5);
    }
}

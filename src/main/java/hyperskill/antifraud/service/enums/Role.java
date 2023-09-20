package hyperskill.antifraud.service.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMINISTRATOR("ADMINISTRATOR"),
    MERCHANT("MERCHANT"),
    SUPPORT("SUPPORT");

    private final String role;

    Role(String role) {
        this.role = role;
    }

}

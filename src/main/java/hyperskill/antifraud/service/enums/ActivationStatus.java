package hyperskill.antifraud.service.enums;

import lombok.Getter;

@Getter
public enum ActivationStatus {
    LOCKED("locked"),
    UNLOCKED("unlocked");

    private final String activationStatus;

    ActivationStatus(String activationStatus) {
        this.activationStatus = activationStatus;
    }

}

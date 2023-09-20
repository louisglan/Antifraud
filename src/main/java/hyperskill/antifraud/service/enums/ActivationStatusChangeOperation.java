package hyperskill.antifraud.service.enums;

import lombok.Getter;

@Getter
public enum ActivationStatusChangeOperation {
    LOCK("LOCK"),
    UNLOCK("UNLOCK");

    private final String operation;

    ActivationStatusChangeOperation(String operation) {
        this.operation = operation;
    }
}

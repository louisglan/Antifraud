package hyperskill.antifraud.service.enums;

import lombok.Getter;

@Getter
public enum TransactionResult {
    ALLOWED("ALLOWED"),
    PROHIBITED("PROHIBITED"),
    MANUAL_PROCESSING("MANUAL_PROCESSING");

    private final String result;

    TransactionResult(String result) {
        this.result = result;
    }

}

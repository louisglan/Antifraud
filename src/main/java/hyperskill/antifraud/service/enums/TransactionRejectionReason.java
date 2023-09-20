package hyperskill.antifraud.service.enums;

import lombok.Getter;

@Getter
public enum TransactionRejectionReason {
    AMOUNT("amount"),
    CARD_NUMBER("card-number"),
    IP("ip"),
    IP_CORRELATION("ip-correlation"),
    REGION_CORRELATION("region-correlation");

    private final String reason;

    TransactionRejectionReason(String reason) {
        this.reason = reason;
    }
}

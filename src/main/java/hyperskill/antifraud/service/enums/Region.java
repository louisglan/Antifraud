package hyperskill.antifraud.service.enums;

import lombok.Getter;

@Getter
public enum Region {
    EAP("EAP"),
    ECA("ECA"),
    HIC("HIC"),
    LAC("LAC"),
    MENA("MENA"),
    SA("SA"),
    SSA("SSA");

    private final String region;
    Region(String region) {
        this.region = region;
    }
}

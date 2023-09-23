package hyperskill.antifraud.service.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IpAddressValidatorTest {
    @Test
    public void testIpAddressLength() {
        Assertions.assertFalse(Validator.isIpInvalid("000.000.000.000"));
        Assertions.assertFalse(Validator.isIpInvalid("00.000.000.000"));
        Assertions.assertFalse(Validator.isIpInvalid("0.000.000.000"));
        Assertions.assertTrue(Validator.isIpInvalid(".000.000.000"));
        Assertions.assertFalse(Validator.isIpInvalid("00.00.00.00"));
        Assertions.assertFalse(Validator.isIpInvalid("0.0.0.0"));
        Assertions.assertTrue(Validator.isIpInvalid("..."));
        Assertions.assertTrue(Validator.isIpInvalid("0000.0.0.0"));
        Assertions.assertTrue(Validator.isIpInvalid("0.0.0.0.0"));
    }

    @Test
    public void testIpNumberRange() {
        Assertions.assertFalse(Validator.isIpInvalid("255.255.255.255"));
        Assertions.assertFalse(Validator.isIpInvalid("199.199.199.199"));
        Assertions.assertTrue(Validator.isIpInvalid("256.255.255.255"));
        Assertions.assertTrue(Validator.isIpInvalid("255.256.255.255"));
        Assertions.assertTrue(Validator.isIpInvalid("255.255.256.255"));
        Assertions.assertTrue(Validator.isIpInvalid("255.255.255.256"));
        Assertions.assertTrue(Validator.isIpInvalid("300.255.255.255"));
        Assertions.assertTrue(Validator.isIpInvalid("255.300.255.255"));
        Assertions.assertTrue(Validator.isIpInvalid("255.255.300.255"));
        Assertions.assertTrue(Validator.isIpInvalid("255.255.255.300"));
    }

    @Test
    public void testLeadingZerosAccepted() {
        Assertions.assertFalse(Validator.isIpInvalid("001.0.0.0"));
    }
}

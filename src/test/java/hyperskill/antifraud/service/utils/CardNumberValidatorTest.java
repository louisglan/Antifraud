package hyperskill.antifraud.service.utils;

import hyperskill.antifraud.service.utils.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CardNumberValidatorTest {
    @Test
    public void testCardNumberLengthIs16() {
        Assertions.assertFalse(Validator.isCardNumberInvalid("1000000000000008"));
        Assertions.assertTrue(Validator.isCardNumberInvalid("100000000000009"));
        Assertions.assertTrue(Validator.isCardNumberInvalid("10000000000000009"));
    }

    @Test
    public void testDoubledDigits() {
        Assertions.assertFalse(Validator.isCardNumberInvalid("6000000000000007"));
        Assertions.assertFalse(Validator.isCardNumberInvalid("0600000000000004"));
    }

    @Test
    public void testMultipleDigitsSum() {
        Assertions.assertFalse(Validator.isCardNumberInvalid("1110000000000005"));
        Assertions.assertFalse(Validator.isCardNumberInvalid("5150000000000007"));
        Assertions.assertFalse(Validator.isCardNumberInvalid("9999000000000004"));
    }

    @Test
    public void testSumToZeroIsCorrect() {
        Assertions.assertFalse(Validator.isCardNumberInvalid("9100000000000000"));
    }
}

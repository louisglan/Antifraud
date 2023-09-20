package hyperskill.antifraud.service.utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    public static boolean isCardNumberInvalid(String number) {
        if (number.length() != 16) {
            return true;
        }
        int lastCharacter = number.charAt(number.length() - 1) - '0';
        String[] payload = number.substring(0, 15).split("");
        int[] payloadValues = Arrays.stream(payload).mapToInt(Integer::parseInt).toArray();
        int payloadSum = 0;
        for (int i = 0; i < payloadValues.length; i++) {
            int valueToSum = payloadValues[i];
            if ((payloadValues.length - i) % 2 != 0) {
                int valueToSumPlaceholder = valueToSum * 2;
                valueToSum = valueToSumPlaceholder > 9 ? 1 + valueToSumPlaceholder % 10 : valueToSumPlaceholder;
            }
            payloadSum += valueToSum;
        }
        return (10 - (payloadSum % 10)) % 10 != lastCharacter;
    }

    public static boolean isIpInvalid(String ip) {
        Pattern pattern = Pattern.compile("^([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])\\.([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])\\.([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])\\.([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])$");
        Matcher matcher = pattern.matcher(ip);
        return !matcher.find();
    }
}

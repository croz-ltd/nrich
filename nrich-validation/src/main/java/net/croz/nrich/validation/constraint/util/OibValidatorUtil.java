package net.croz.nrich.validation.constraint.util;

public final class OibValidatorUtil {

    private OibValidatorUtil() {
    }

    public static boolean validOib(String oib) {
        if (!oib.matches("\\d{11}")) {
            return false;
        }

        int modulo = 10;
        for (int i = 0; i < 10; i++) {
            modulo += Integer.parseInt(oib.substring(i, i + 1));
            modulo %= 10;
            modulo = (modulo != 0) ? modulo : 10;
            modulo *= 2;
            modulo %= 11;
        }

        int controlNumber = 11 - modulo;
        controlNumber = (controlNumber != 10) ? controlNumber : 0;

        return controlNumber == Integer.parseInt(oib.substring(10));
    }
}

package net.croz.nrich.validation.constraint.util;

public final class OibValidatorUtil {

    private OibValidatorUtil() {
    }

    public static boolean validOib(String oib) {
        if (!oib.matches("\\d{11}")) {
            return false;
        }

        int a = 10;
        for (int i = 0; i < 10; i++) {
            a += Integer.parseInt(oib.substring(i, i + 1));
            a %= 10;
            a = (a != 0) ? a : 10;
            a *= 2;
            a %= 11;
        }

        int kontrolni = 11 - a;
        kontrolni = (kontrolni != 10) ? kontrolni : 0;

        return kontrolni == Integer.parseInt(oib.substring(10));
    }

}

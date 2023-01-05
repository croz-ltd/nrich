/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.validation.constraint.util;

public final class OibValidatorUtil {

    private static final String ELEVEN_DIGITS_REGEX = "\\d{11}";

    private OibValidatorUtil() {
    }

    public static boolean validOib(String oib) {
        if (!oib.matches(ELEVEN_DIGITS_REGEX)) {
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

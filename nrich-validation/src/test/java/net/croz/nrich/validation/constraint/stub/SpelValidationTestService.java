package net.croz.nrich.validation.constraint.stub;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpelValidationTestService {

    private static final Pattern PATTERN = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

    public boolean validateUuid(String uuid) {
        Matcher matcher = PATTERN.matcher(uuid);
        return matcher.find();
    }
}

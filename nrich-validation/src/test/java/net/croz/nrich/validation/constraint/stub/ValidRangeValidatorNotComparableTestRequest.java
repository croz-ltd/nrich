package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidRange;

@RequiredArgsConstructor
@Getter
@ValidRange(fromPropertyName = "firstField", toPropertyName = "secondField")
public class ValidRangeValidatorNotComparableTestRequest {

    private final NotComparable firstField;

    private final NotComparable secondField;

    public static class NotComparable {

    }
}

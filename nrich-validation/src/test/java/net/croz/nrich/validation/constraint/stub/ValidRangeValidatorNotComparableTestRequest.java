package net.croz.nrich.validation.constraint.stub;

import lombok.Data;
import net.croz.nrich.validation.api.constraint.ValidRange;

@Data
@ValidRange(fromPropertyName = "firstField", toPropertyName = "secondField")
public class ValidRangeValidatorNotComparableTestRequest {

    private final NotComparable firstField;

    private final NotComparable secondField;

    public static class NotComparable {

    }
}

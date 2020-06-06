package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidRange;

@RequiredArgsConstructor
@Getter
@ValidRange(fromPropertyName = "firstField", toPropertyName = "secondField", inclusive = true)
public class ValidRangeValidatorInclusiveTestRequest {

    private final Integer firstField;

    private final Integer secondField;

}

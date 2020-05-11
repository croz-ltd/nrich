package net.croz.nrich.validation.constraint.stub;

import lombok.Data;
import net.croz.nrich.validation.api.constraint.ValidRange;

@Data
@ValidRange(fromPropertyName = "firstField", toPropertyName = "secondField", inclusive = true)
public class ValidRangeValidatorInclusiveTestRequest {

    private final Integer firstField;

    private final Integer secondField;

}

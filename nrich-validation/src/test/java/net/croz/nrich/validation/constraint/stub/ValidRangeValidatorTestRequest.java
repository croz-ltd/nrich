package net.croz.nrich.validation.constraint.stub;

import lombok.Data;
import net.croz.nrich.validation.api.constraint.ValidRange;

import java.time.Instant;

@Data
@ValidRange(fromPropertyName = "firstField", toPropertyName = "secondField")
public class ValidRangeValidatorTestRequest {

    private final Instant firstField;

    private final Instant secondField;

}

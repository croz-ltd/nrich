package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidRange;

import java.time.Instant;

@RequiredArgsConstructor
@Getter
@ValidRange(fromPropertyName = "firstField", toPropertyName = "secondField")
public class ValidRangeValidatorTestRequest {

    private final Instant firstField;

    private final Instant secondField;

}

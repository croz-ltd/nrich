package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidRange;

import java.time.Instant;

@RequiredArgsConstructor
@Getter
@ValidRange(fromPropertyName = "firstField", toPropertyName = "secondField")
public class ValidRangeValidatorDifferentTypeTestRequest {

    private final Long firstField;

    private final Instant secondField;

}

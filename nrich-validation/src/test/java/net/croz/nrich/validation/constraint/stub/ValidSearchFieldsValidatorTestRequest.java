package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidSearchProperties;

import java.time.Instant;

@RequiredArgsConstructor
@Getter
@ValidSearchProperties(propertyGroup = { @ValidSearchProperties.PropertyGroup({ "firstGroupFieldOne", "firstGroupFieldTwo" }), @ValidSearchProperties.PropertyGroup("secondGroupFieldOne") })
public class ValidSearchFieldsValidatorTestRequest {

    private final String firstGroupFieldOne;

    private final Instant firstGroupFieldTwo;

    private final Boolean secondGroupFieldOne;

}

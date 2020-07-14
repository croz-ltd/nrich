package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidFileResolvable;

@RequiredArgsConstructor
@Getter
public class ValidFileResolvableValidatorInvalidTypeFileTestRequest {

    @ValidFileResolvable
    private final Object file;

}

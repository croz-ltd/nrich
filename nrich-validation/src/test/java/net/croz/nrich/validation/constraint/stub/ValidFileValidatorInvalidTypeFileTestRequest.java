package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidFile;

@RequiredArgsConstructor
@Getter
public class ValidFileValidatorInvalidTypeFileTestRequest {

    @ValidFile
    private final Object file;

}

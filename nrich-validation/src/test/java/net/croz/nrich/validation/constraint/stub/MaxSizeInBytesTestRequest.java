package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.MaxSizeInBytes;

@RequiredArgsConstructor
@Getter
public class MaxSizeInBytesTestRequest {

    @MaxSizeInBytes(value = 5L)
    private final String value;

}

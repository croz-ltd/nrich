package net.croz.nrich.validation.constraint.stub;

import lombok.Data;
import net.croz.nrich.validation.api.constraint.MaxSizeInBytes;

@Data
public class MaxSizeInBytesTestRequest {

    @MaxSizeInBytes(value = 5L)
    private final String value;

}

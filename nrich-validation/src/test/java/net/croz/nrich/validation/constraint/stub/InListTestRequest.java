package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.InList;

@RequiredArgsConstructor
@Getter
public class InListTestRequest {

    @InList(value = "in list")
    private final String value;

}

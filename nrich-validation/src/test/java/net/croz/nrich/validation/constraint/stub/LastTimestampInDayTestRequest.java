package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.LastTimestampInDay;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
public class LastTimestampInDayTestRequest {

    @LastTimestampInDay
    private final LocalDate localDate;
}

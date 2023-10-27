package net.croz.nrich.validation.constraint.stub;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.MinDate;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
public class MinDateTestRequest {

    @MinDate("2023-10-10")
    private final LocalDate date;
}

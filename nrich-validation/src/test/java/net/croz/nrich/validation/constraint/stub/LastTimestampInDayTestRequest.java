package net.croz.nrich.validation.constraint.stub;

import net.croz.nrich.validation.api.constraint.LastTimestampInDay;

import java.time.LocalDate;

public record LastTimestampInDayTestRequest(@LastTimestampInDay LocalDate localDate) {

}

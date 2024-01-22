package net.croz.nrich.validation.constraint.stub;


import net.croz.nrich.validation.api.constraint.MinDate;

import java.time.LocalDate;

public record MinDateTestRequest(@MinDate("2023-10-10") LocalDate date) {

}

/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.excel.model;

import org.apache.poi.ss.usermodel.Cell;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PoiCellHolderTest {

    @Mock
    private Cell cell;

    @InjectMocks
    private PoiCellHolder poiCellHolder;

    @Test
    void shouldReturnColumnIndex() {
        // given
        Integer value = 1;
        doReturn(value).when(cell).getColumnIndex();

        // when
        Integer result = poiCellHolder.getColumnIndex();

        // then
        assertThat(result).isEqualTo(value);
    }

    @Test
    void shouldReturnRowIndex() {
        // given
        Integer value = 1;
        doReturn(value).when(cell).getRowIndex();

        // when
        Integer result = poiCellHolder.getRowIndex();

        // then
        assertThat(result).isEqualTo(value);
    }

    @SuppressWarnings("java:S2699")
    @MethodSource("shouldSetCellValueMethodSource")
    @ParameterizedTest
    void shouldSetCellValue(Object value, Consumer<Cell> verificationFunction) {
        // when
        poiCellHolder.setCellValue(value);

        // then
        verificationFunction.accept(cell);
    }

    private static Stream<Arguments> shouldSetCellValueMethodSource() {
        boolean booleanValue = true;
        int numberValue = 10;
        Date dateValue = new Date();
        Calendar calendarValue = Calendar.getInstance();
        LocalDateTime localDateTimeValue = LocalDateTime.now();
        LocalDate localDateValue = LocalDate.now();
        String stringValue = "value";

        return Stream.of(
            arguments(booleanValue, (Consumer<Cell>) currentCell -> verify(currentCell).setCellValue(booleanValue)),
            arguments(numberValue, (Consumer<Cell>) currentCell -> verify(currentCell).setCellValue(numberValue)),
            arguments(dateValue, (Consumer<Cell>) currentCell -> verify(currentCell).setCellValue(dateValue)),
            arguments(calendarValue, (Consumer<Cell>) currentCell -> verify(currentCell).setCellValue(calendarValue)),
            arguments(localDateTimeValue, (Consumer<Cell>) currentCell -> verify(currentCell).setCellValue(localDateTimeValue)),
            arguments(localDateValue, (Consumer<Cell>) currentCell -> verify(currentCell).setCellValue(localDateValue)),
            arguments(stringValue, (Consumer<Cell>) currentCell -> verify(currentCell).setCellValue(stringValue))
        );
    }

    @Test
    void shouldThrowExceptionOnUnrecognizedCellValueType() {
        // when
        Throwable throwable = catchThrowable(() -> poiCellHolder.setCellValue(new Object()));

        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Set cell value called with unrecognized type!");
    }
}

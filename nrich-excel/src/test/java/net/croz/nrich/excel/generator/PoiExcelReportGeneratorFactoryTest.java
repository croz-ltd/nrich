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

package net.croz.nrich.excel.generator;

import net.croz.nrich.excel.ExcelTestConfiguration;
import net.croz.nrich.excel.api.generator.ExcelReportGenerator;
import net.croz.nrich.excel.api.request.CreateReportGeneratorRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(ExcelTestConfiguration.class)
class PoiExcelReportGeneratorFactoryTest {

    @Autowired
    private PoiExcelReportGeneratorFactory excelReportGeneratorFactory;

    @Test
    void shouldThrowExceptionWhenOutputFileDoesntExist() {
        // given
        CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().build();

        // when
        Throwable thrown = catchThrowable(() -> excelReportGeneratorFactory.createReportGenerator(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("OutputStream cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenTemplateFileDoesntExist() {
        // given
        OutputStream outputStream = new ByteArrayOutputStream();
        CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().outputStream(outputStream).build();

        // when
        Throwable thrown = catchThrowable(() -> excelReportGeneratorFactory.createReportGenerator(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Template path cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenRowIndexIsNegative() {
        // given
        OutputStream outputStream = new ByteArrayOutputStream();
        CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().outputStream(outputStream).templatePath("classpath:excel/template.xlsx").firstRowIndex(-1).build();

        // when
        Throwable thrown = catchThrowable(() -> excelReportGeneratorFactory.createReportGenerator(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Row index must be greater or equal to 0");
    }

    @Test
    void shouldReturnReportGeneratorWhenDataIsValid() {
        // given
        OutputStream outputStream = new ByteArrayOutputStream();
        CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().outputStream(outputStream).templatePath("classpath:excel/template.xlsx").firstRowIndex(1).build();

        // when
        ExcelReportGenerator excelReportGenerator = excelReportGeneratorFactory.createReportGenerator(request);

        // then
        assertThat(excelReportGenerator).isNotNull();
    }
}

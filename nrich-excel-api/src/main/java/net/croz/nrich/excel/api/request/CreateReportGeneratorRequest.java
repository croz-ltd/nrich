/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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

package net.croz.nrich.excel.api.request;

import lombok.Builder;
import lombok.Getter;
import net.croz.nrich.excel.api.model.ColumnDataFormat;
import net.croz.nrich.excel.api.model.TemplateVariable;

import java.io.OutputStream;
import java.util.List;

@Getter
@Builder
public class CreateReportGeneratorRequest {

    /**
     * OutputStream where report will be written to (keep in mind closing of it is users responsibility).
     */
    private final OutputStream outputStream;

    /**
     * Path to template (template is resolved from this path using Spring's ResourceLoader).
     */
    private final String templatePath;

    /**
     * List of {@link TemplateVariable} instances that will be used to replace variables defined in template.
     */
    private final List<TemplateVariable> templateVariableList;

    /**
     * List of {@link ColumnDataFormat} instances that allow for overriding of data format for specific columns.
     */
    private final List<ColumnDataFormat> columnDataFormatList;

    /**
     * Row index from which data should be written to report (if for example template holds column headers in first a couple of rows).
     */
    private final int firstRowIndex;

}

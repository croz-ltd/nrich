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

package net.croz.nrich.excel.api.model;

/**
 * Holds data format for excel column. Used when overriding default data format for specific column.
 *
 * @param columnIndex Column index.
 * @param dataFormat  Column data format (i.e dd.MM.yyyy, #,##0.00 etc.).
 */
public record ColumnDataFormat(int columnIndex, String dataFormat) {

}

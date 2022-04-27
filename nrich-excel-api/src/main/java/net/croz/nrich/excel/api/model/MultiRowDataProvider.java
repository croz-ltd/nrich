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

package net.croz.nrich.excel.api.model;

/**
 * Provides an array of rows that will be written to excel. Each row is represented as an array of objects.
 */
@FunctionalInterface
public interface MultiRowDataProvider {

    /**
     * Returns an array of rows to be written to excel report. Method is called with incrementing start argument until it returns null or empty array.
     *
     * @param start index of first row
     * @param limit how many rows should be returned
     * @return an array of rows to be written to excel report
     */
    Object[][] resolveMultiRowData(int start, int limit);

}

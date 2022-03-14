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

package net.croz.nrich.excel.api.converter;

import net.croz.nrich.excel.api.model.CellHolder;

/**
 * Optionally converts and set values on {@link CellHolder} instance.
 */
public interface CellValueConverter {

    /**
     * Set cell value, perform conversion if necessary.
     *
     * @param cell  cell to set value on
     * @param value value to set
     */
    void setCellValue(CellHolder cell, Object value);

    /**
     * Returns true if this converter supports cell and value combination.
     *
     * @param cell  cell that value will be set
     * @param value value to be set
     * @return whether cell and value combination is supported
     */
    boolean supports(CellHolder cell, Object value);

}

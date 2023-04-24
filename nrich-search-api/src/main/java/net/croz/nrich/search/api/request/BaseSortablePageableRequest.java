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

package net.croz.nrich.search.api.request;

import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.model.sort.SortProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Helper class for creating request that support paging and sorting.
 */
@Setter
@Getter
public abstract class BaseSortablePageableRequest implements SortablePageableRequest {

    /**
     * Page number.
     */
    @Min(0)
    @NotNull
    private Integer pageNumber;

    /**
     * Number of elements to fetch.
     */
    @Min(1)
    @NotNull
    private Integer pageSize;

    /**
     * List of properties to sort by.
     */
    private List<SortProperty> sortPropertyList;

}

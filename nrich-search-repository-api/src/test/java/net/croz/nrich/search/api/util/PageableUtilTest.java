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

package net.croz.nrich.search.api.util;

import net.croz.nrich.search.api.model.sort.SortDirection;
import net.croz.nrich.search.api.model.sort.SortProperty;
import net.croz.nrich.search.api.util.stub.DefaultPageableSortableTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageableUtilTest {

    @Test
    void shouldConvertPageableSortableRequestWithUniqueSortPropertyToPageable() {
        // given
        int pageNumber = 0;
        int pageSize = 10;
        String sortProperty = "name";
        String uniqueSortProperty = "id";
        List<SortProperty> sortPropertyList = Collections.singletonList(new SortProperty(sortProperty, SortDirection.DESC));
        DefaultPageableSortableTestRequest request = new DefaultPageableSortableTestRequest(pageNumber, pageSize, sortPropertyList);

        // when
        Pageable pageable = PageableUtil.convertToPageable(request, new SortProperty(uniqueSortProperty, SortDirection.ASC));

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(sortProperty)).isEqualTo(Sort.Order.desc(sortProperty));
        assertThat(pageable.getSort().getOrderFor(uniqueSortProperty)).isEqualTo(Sort.Order.asc(uniqueSortProperty));
    }

    @Test
    void shouldConvertPageableSortableRequestToPageable() {
        // given
        int pageNumber = 0;
        int pageSize = 10;
        String sortProperty = "name";
        List<SortProperty> sortPropertyList = Collections.singletonList(new SortProperty(sortProperty, SortDirection.DESC));
        DefaultPageableSortableTestRequest request = new DefaultPageableSortableTestRequest(pageNumber, pageSize, sortPropertyList);

        // when
        Pageable pageable = PageableUtil.convertToPageable(request);

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(sortProperty)).isEqualTo(Sort.Order.desc(sortProperty));
    }

    @Test
    void shouldConvertUnsortedPageDataToPageable() {
        // given
        int pageNumber = 0;
        int pageSize = 10;

        // when
        Pageable pageable = PageableUtil.convertToPageable(pageNumber, pageSize);

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);
    }

    @Test
    void shouldConvertPageDataWithUniqueSortPropertyToPageable() {
        // given
        int pageNumber = 0;
        int pageSize = 10;
        String uniqueSortProperty = "id";
        List<SortProperty> sortPropertyList = Collections.singletonList(new SortProperty(uniqueSortProperty, SortDirection.ASC));

        // when
        Pageable pageable = PageableUtil.convertToPageable(pageNumber, pageSize, sortPropertyList);

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(uniqueSortProperty)).isEqualTo(Sort.Order.asc(uniqueSortProperty));
    }

    @Test
    void shouldConvertPageDataWithSortPropertyToListPageable() {
        // given
        int pageNumber = 0;
        int pageSize = 10;
        String sortProperty = "name";
        List<SortProperty> sortPropertyList = Collections.singletonList(new SortProperty(sortProperty, SortDirection.DESC));

        // when
        Pageable pageable = PageableUtil.convertToPageable(pageNumber, pageSize, sortPropertyList);

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(sortProperty)).isEqualTo(Sort.Order.desc(sortProperty));
    }

    @Test
    void shouldConvertPageDataWithUniqueSortPropertyAndSortPropertyToListPageable() {
        // given
        int pageNumber = 0;
        int pageSize = 10;
        String uniqueSortProperty = "id";
        String sortProperty = "name";
        List<SortProperty> sortPropertyList = Collections.singletonList(new SortProperty(sortProperty, SortDirection.DESC));

        // when
        Pageable pageable = PageableUtil.convertToPageable(pageNumber, pageSize, new SortProperty(uniqueSortProperty, SortDirection.ASC), sortPropertyList);

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(uniqueSortProperty)).isEqualTo(Sort.Order.asc(uniqueSortProperty));
        assertThat(pageable.getSort().getOrderFor(sortProperty)).isEqualTo(Sort.Order.desc(sortProperty));
    }
}

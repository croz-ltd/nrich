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

    private static final int PAGE_NUMBER = 0;

    private static final int PAGE_SIZE = 10;

    private static final String SORT_PROPERTY = "name";

    private static final String UNIQUE_SORT_PROPERTY = "id";

    @Test
    void shouldConvertPageableSortableRequestWithUniqueSortPropertyToPageable() {
        // given
        List<SortProperty> sortPropertyList = Collections.singletonList(new SortProperty(SORT_PROPERTY, SortDirection.DESC));
        DefaultPageableSortableTestRequest request = new DefaultPageableSortableTestRequest(PAGE_NUMBER, PAGE_SIZE, sortPropertyList);

        // when
        Pageable pageable = PageableUtil.convertToPageable(request, new SortProperty(UNIQUE_SORT_PROPERTY, SortDirection.ASC));

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(PAGE_NUMBER);
        assertThat(pageable.getPageSize()).isEqualTo(PAGE_SIZE);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(SORT_PROPERTY)).isEqualTo(Sort.Order.desc(SORT_PROPERTY));
        assertThat(pageable.getSort().getOrderFor(UNIQUE_SORT_PROPERTY)).isEqualTo(Sort.Order.asc(UNIQUE_SORT_PROPERTY));
    }

    @Test
    void shouldConvertPageableSortableRequestToPageable() {
        // given
        List<SortProperty> sortPropertyList = Collections.singletonList(new SortProperty(SORT_PROPERTY, SortDirection.DESC));
        DefaultPageableSortableTestRequest request = new DefaultPageableSortableTestRequest(PAGE_NUMBER, PAGE_SIZE, sortPropertyList);

        // when
        Pageable pageable = PageableUtil.convertToPageable(request);

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(PAGE_NUMBER);
        assertThat(pageable.getPageSize()).isEqualTo(PAGE_SIZE);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(SORT_PROPERTY)).isEqualTo(Sort.Order.desc(SORT_PROPERTY));
    }

    @Test
    void shouldConvertUnsortedPageDataToPageable() {
        // when
        Pageable pageable = PageableUtil.convertToPageable(PAGE_NUMBER, PAGE_SIZE);

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(PAGE_NUMBER);
        assertThat(pageable.getPageSize()).isEqualTo(PAGE_SIZE);
    }

    @Test
    void shouldConvertPageDataWithUniqueSortPropertyToPageable() {
        // given
        List<SortProperty> sortPropertyList = Collections.singletonList(new SortProperty(UNIQUE_SORT_PROPERTY, SortDirection.ASC));

        // when
        Pageable pageable = PageableUtil.convertToPageable(PAGE_NUMBER, PAGE_SIZE, sortPropertyList);

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(PAGE_NUMBER);
        assertThat(pageable.getPageSize()).isEqualTo(PAGE_SIZE);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(UNIQUE_SORT_PROPERTY)).isEqualTo(Sort.Order.asc(UNIQUE_SORT_PROPERTY));
    }

    @Test
    void shouldConvertPageDataWithSortPropertyToListPageable() {
        // given
        List<SortProperty> sortPropertyList = Collections.singletonList(new SortProperty(SORT_PROPERTY, SortDirection.DESC));

        // when
        Pageable pageable = PageableUtil.convertToPageable(PAGE_NUMBER, PAGE_SIZE, sortPropertyList);

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(PAGE_NUMBER);
        assertThat(pageable.getPageSize()).isEqualTo(PAGE_SIZE);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(SORT_PROPERTY)).isEqualTo(Sort.Order.desc(SORT_PROPERTY));
    }

    @Test
    void shouldConvertPageDataWithUniqueSortPropertyAndSortPropertyToListPageable() {
        // given
        List<SortProperty> sortPropertyList = Collections.singletonList(new SortProperty(SORT_PROPERTY, SortDirection.DESC));

        // when
        Pageable pageable = PageableUtil.convertToPageable(PAGE_NUMBER, PAGE_SIZE, new SortProperty(UNIQUE_SORT_PROPERTY, SortDirection.ASC), sortPropertyList);

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(PAGE_NUMBER);
        assertThat(pageable.getPageSize()).isEqualTo(PAGE_SIZE);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(UNIQUE_SORT_PROPERTY)).isEqualTo(Sort.Order.asc(UNIQUE_SORT_PROPERTY));
        assertThat(pageable.getSort().getOrderFor(SORT_PROPERTY)).isEqualTo(Sort.Order.desc(SORT_PROPERTY));
    }

    @Test
    void shouldConvertToPageableWithUniqueSortProperty() {
        // when
        Pageable pageable = PageableUtil.convertToPageable(PAGE_NUMBER, PAGE_SIZE, new SortProperty(UNIQUE_SORT_PROPERTY, SortDirection.ASC));

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(PAGE_NUMBER);
        assertThat(pageable.getPageSize()).isEqualTo(PAGE_SIZE);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(UNIQUE_SORT_PROPERTY)).isEqualTo(Sort.Order.asc(UNIQUE_SORT_PROPERTY));
    }
}

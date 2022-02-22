package net.croz.nrich.search.api.util;

import net.croz.nrich.search.api.model.sort.SortDirection;
import net.croz.nrich.search.api.model.sort.SortProperty;
import net.croz.nrich.search.api.util.stub.DefaultPageableSortableTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class PageableUtilTest {

    @Test
    void shouldConvertPageableSortableRequestWithUniqueSortPropertyToPageable() {
        // given
        int pageNumber = 0;
        int pageSize = 10;
        String sortProperty = "name";
        String uniqueSortProperty = "id";
        DefaultPageableSortableTestRequest request = new DefaultPageableSortableTestRequest(pageNumber, pageSize, Collections.singletonList(new SortProperty(sortProperty, SortDirection.DESC)));

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
        DefaultPageableSortableTestRequest request = new DefaultPageableSortableTestRequest(pageNumber, pageSize, Collections.singletonList(new SortProperty(sortProperty, SortDirection.DESC)));

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

        // when
        Pageable pageable = PageableUtil.convertToPageable(pageNumber, pageSize, new SortProperty(uniqueSortProperty, SortDirection.ASC));

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

        // when
        Pageable pageable = PageableUtil.convertToPageable(pageNumber, pageSize, Collections.singletonList(new SortProperty(sortProperty, SortDirection.DESC)));

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

        // when
        Pageable pageable = PageableUtil.convertToPageable(pageNumber, pageSize, new SortProperty(uniqueSortProperty, SortDirection.ASC), Collections.singletonList(new SortProperty(sortProperty, SortDirection.DESC)));

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(uniqueSortProperty)).isEqualTo(Sort.Order.asc(uniqueSortProperty));
        assertThat(pageable.getSort().getOrderFor(sortProperty)).isEqualTo(Sort.Order.desc(sortProperty));
    }
}

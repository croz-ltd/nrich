package net.croz.nrich.search.api.util;

import net.croz.nrich.search.api.model.sort.SortDirection;
import net.croz.nrich.search.api.model.sort.SortProperty;
import net.croz.nrich.search.api.util.stub.DefaultPageableSortableTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class PageableUtilTest {

    @Test
    void shouldConvertPageableSortableRequestWithUniqueSortPropertyToPageable() {
        // given
        final int pageNumber = 0;
        final int pageSize = 10;
        final String sortProperty = "name";
        final String uniqueSortProperty = "id";
        final DefaultPageableSortableTestRequest request = new DefaultPageableSortableTestRequest(pageNumber, pageSize, Collections.singletonList(new SortProperty(sortProperty, SortDirection.DESC)));

        // when
        final Pageable pageable = PageableUtil.convertToPageable(request, new SortProperty(uniqueSortProperty, SortDirection.ASC));

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
        final int pageNumber = 0;
        final int pageSize = 10;
        final String sortProperty = "name";
        final DefaultPageableSortableTestRequest request = new DefaultPageableSortableTestRequest(pageNumber, pageSize, Collections.singletonList(new SortProperty(sortProperty, SortDirection.DESC)));

        // when
        final Pageable pageable = PageableUtil.convertToPageable(request);

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
        final int pageNumber = 0;
        final int pageSize = 10;

        // when
        final Pageable pageable = PageableUtil.convertToPageable(pageNumber, pageSize);

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);
    }

    @Test
    void shouldConvertPageDataWithUniqueSortPropertyToPageable() {
        // given
        final int pageNumber = 0;
        final int pageSize = 10;
        final String uniqueSortProperty = "id";

        // when
        final Pageable pageable = PageableUtil.convertToPageable(pageNumber, pageSize, new SortProperty(uniqueSortProperty, SortDirection.ASC));

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
        final int pageNumber = 0;
        final int pageSize = 10;
        final String sortProperty = "name";

        // when
        final Pageable pageable = PageableUtil.convertToPageable(pageNumber, pageSize, Collections.singletonList(new SortProperty(sortProperty, SortDirection.DESC)));

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
        final int pageNumber = 0;
        final int pageSize = 10;
        final String uniqueSortProperty = "id";
        final String sortProperty = "name";

        // when
        final Pageable pageable = PageableUtil.convertToPageable(pageNumber, pageSize, new SortProperty(uniqueSortProperty, SortDirection.ASC), Collections.singletonList(new SortProperty(sortProperty, SortDirection.DESC)));

        // then
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(uniqueSortProperty)).isEqualTo(Sort.Order.asc(uniqueSortProperty));
        assertThat(pageable.getSort().getOrderFor(sortProperty)).isEqualTo(Sort.Order.desc(sortProperty));
    }
}

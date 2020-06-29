package net.croz.nrich.search.api.util;

import net.croz.nrich.search.api.model.sort.SortDirection;
import net.croz.nrich.search.api.model.sort.SortProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

// TODO maybe another project? search-util?
// unique sort property is here since some databases do not guarantee consistent paging if sorting by non unique properties, it is defined also as a separate arguments since it probably won't be sent from client
public final class PageableUtil {

    private PageableUtil() {
    }

    public static Pageable convertToPageable(final Integer pageNumber, final Integer pageSize) {
        return convertToPageable(pageNumber, pageSize, null, null);
    }

    public static Pageable convertToPageable(final Integer pageNumber, final Integer pageSize, final SortProperty uniqueSortProperty) {
        return convertToPageable(pageNumber, pageSize, uniqueSortProperty, null);
    }

    public static Pageable convertToPageable(final Integer pageNumber, final Integer pageSize, final List<SortProperty> sortPropertyList) {
        return convertToPageable(pageNumber, pageSize, null, sortPropertyList);
    }

    public static Pageable convertToPageable(final Integer pageNumber, final Integer pageSize, final SortProperty uniqueSortProperty, final List<SortProperty> sortPropertyList) {
        Sort.Order uniqueSortOrder = null;
        if (uniqueSortProperty != null) {
            uniqueSortOrder = convertToSortOrder(uniqueSortProperty);
        }

        final Sort sort;
        if (CollectionUtils.isEmpty(sortPropertyList)) {
            if (uniqueSortOrder == null) {
                sort = Sort.unsorted();
            }
            else {
                sort = Sort.by(uniqueSortOrder);
            }
        }
        else {
            final List<Sort.Order> orderList = sortPropertyList.stream()
                    .map(PageableUtil::convertToSortOrder)
                    .collect(Collectors.toList());

            if (uniqueSortOrder != null && sortPropertyList.stream().map(SortProperty::getProperty).noneMatch(value -> value.equals(uniqueSortProperty.getProperty()))) {
                orderList.add(uniqueSortOrder);
            }

            sort = Sort.by(orderList);
        }

        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private static Sort.Order convertToSortOrder(final SortProperty sortProperty) {
        return sortProperty.getDirection() == SortDirection.ASC ? Sort.Order.asc(sortProperty.getProperty()) : Sort.Order.desc(sortProperty.getProperty());
    }
}

package net.croz.nrich.search.util;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.util.Assert;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds support for sorting by subclass properties. Spring Data resolves sort properties only against the queried entity type,
 * and since Hibernate 7 subclass attributes have to be accessed through an explicit treat (downcast). The {@link From} is treated
 * as the subtype declaring the first property segment when necessary, the rest is delegated to Spring Data.
 */
public final class NrichQueryUtil {

    private NrichQueryUtil() {
    }

    public static List<Order> toOrders(Sort sort, From<?, ?> from, CriteriaBuilder criteriaBuilder) {
        Assert.notNull(from, "From must not be null");

        List<Order> orders = new ArrayList<>();

        for (Sort.Order order : sort) {
            String firstPathSegment = PathResolvingUtil.convertToPathList(order.getProperty())[0];
            From<?, ?> castedFrom = PathResolvingUtil.castPathIfNecessary(from, firstPathSegment);

            orders.addAll(QueryUtils.toOrders(Sort.by(order), castedFrom, criteriaBuilder));
        }

        return orders;
    }
}

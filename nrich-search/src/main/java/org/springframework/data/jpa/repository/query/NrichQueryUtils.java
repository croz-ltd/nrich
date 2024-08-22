package org.springframework.data.jpa.repository.query;

import org.hibernate.metamodel.model.domain.EntityDomainType;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.metamodel.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Adds support for sorting by subclass properties. Although Hibernate supports sorting by subclass properties, Spring will throw an exception.
 */
public final class NrichQueryUtils {

    private static final Map<PropertyPathKey, PropertyPath> PROPERTY_PATH_CACHE = new ConcurrentReferenceHashMap<>();

    private NrichQueryUtils() {
    }

    public static List<Order> toOrders(Sort sort, From<?, ?> from, CriteriaBuilder cb) {
        if (sort.isUnsorted()) {
            return Collections.emptyList();
        }

        Assert.notNull(from, "From must not be null");
        Assert.notNull(cb, "CriteriaBuilder must not be null");

        List<jakarta.persistence.criteria.Order> orders = new ArrayList<>();

        for (org.springframework.data.domain.Sort.Order order : sort) {
            orders.add(toJpaOrder(order, from, cb));
        }

        return orders;
    }

    private static jakarta.persistence.criteria.Order toJpaOrder(Sort.Order order, From<?, ?> from, CriteriaBuilder cb) {
        String propertyName = order.getProperty();

        PropertyPath property = PROPERTY_PATH_CACHE.computeIfAbsent(new PropertyPathKey(propertyName, from.getJavaType()), key -> resolvePropertyPath(propertyName, from));
        Expression<?> expression = QueryUtils.toExpressionRecursively(from, property);

        if (order.isIgnoreCase() && String.class.equals(expression.getJavaType())) {
            @SuppressWarnings("unchecked")
            Expression<String> upper = cb.lower((Expression<String>) expression);
            return order.isAscending() ? cb.asc(upper) : cb.desc(upper);
        }

        return order.isAscending() ? cb.asc(expression) : cb.desc(expression);
    }

    private static PropertyPath resolvePropertyPath(String property, From<?, ?> from) {
        PropertyReferenceException originalException;
        try {
            return PropertyPath.from(property, from.getJavaType());
        }
        catch (PropertyReferenceException exception) {
            originalException = exception;
        }

        List<? extends Class<?>> subtypeList = resolveSubtypeList(from);
        if (!CollectionUtils.isEmpty(subtypeList)) {
            for (Class<?> subtype : subtypeList) {
                try {
                    return PropertyPath.from(property, subtype);
                }
                catch (PropertyReferenceException exception) {
                    // ignored
                }
            }
        }

        throw originalException;
    }

    private static List<? extends Class<?>> resolveSubtypeList(From<?, ?> from) {
        if (from.getModel() instanceof EntityDomainType<?> entityDomainType) {
            return entityDomainType.getSubTypes().stream()
                .map(Type::getJavaType)
                .toList();
        }

        return Collections.emptyList();
    }

    record PropertyPathKey(String property, Class<?> type) {
    }
}

package net.croz.nrich.search.repository.impl;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.model.SearchConfiguration;
import net.croz.nrich.search.repository.StringSearchExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class JpaStringSearchExecutor<T> implements StringSearchExecutor<T> {

    private final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    private final JpaSearchExecutor<T> jpaSearchExecutor;

    private final JpaEntityInformation<T, ?> jpaEntityInformation;

    @Override
    public <P> Optional<P> findOne(final String searchTerm, final List<String> propertyToSearchList, final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        final Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList);

        return jpaSearchExecutor.findOne(searchMap, searchConfiguration);
    }

    @Override
    public <P> List<P> findAll(final String searchTerm, final List<String> propertyToSearchList, final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        final Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList);

        return jpaSearchExecutor.findAll(searchMap, searchConfiguration);
    }

    @Override
    public <P> List<P> findAll(final String searchTerm, final List<String> propertyToSearchList, final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration, final Sort sort) {
        final Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList);

        return jpaSearchExecutor.findAll(searchMap, searchConfiguration, sort);
    }

    @Override
    public <P> Page<P> findAll(final String searchTerm, final List<String> propertyToSearchList, final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration, final Pageable pageable) {
        final Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList);

        return jpaSearchExecutor.findAll(searchMap, searchConfiguration, pageable);
    }

    @Override
    public <P> long count(final String searchTerm, final List<String> propertyToSearchList, final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        final Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList);

        return jpaSearchExecutor.count(searchMap, searchConfiguration);
    }

    @Override
    public <P> boolean exists(final String searchTerm, final List<String> propertyToSearchList, final SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        final Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList);

        return jpaSearchExecutor.exists(searchMap, searchConfiguration);
    }

    private Map<String, Object> convertToMap(final String searchTerm, final List<String> propertyToSearchList) {
        return stringToEntityPropertyMapConverter.convert(searchTerm, propertyToSearchList, jpaEntityInformation.getRequiredIdAttribute().getDeclaringType());
    }
}

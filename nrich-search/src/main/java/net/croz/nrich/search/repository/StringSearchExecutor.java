package net.croz.nrich.search.repository;

import net.croz.nrich.search.model.SearchConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StringSearchExecutor<T> {

    <P> Optional<P> findOne(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration);

    <P> List<P> findAll(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration);

    <P> List<P> findAll(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration, Sort sort);

    <P> Page<P> findAll(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration, Pageable pageable);

    <P> long count(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration);

    <P> boolean exists(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration);
}

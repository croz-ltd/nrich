package net.croz.nrich.search.api.repository;

import net.croz.nrich.search.api.model.SearchConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * Allows for execution of queries built from search request and configured by search configuration.
 *
 * @param <T> entity type
 */
public interface SearchExecutor<T> {

    /**
     * Returns a single entity that matches conditions applied from search request or {@link Optional#empty()} if none was found.
     *
     * @param request search request that contains query values
     * @param searchConfiguration configuration that decides how query should be built
     * @return a single entity matching conditions or {@link Optional#empty()} if none was found.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if the query returns more than one
     *           result.
     */
    <R, P> Optional<P> findOne(R request, SearchConfiguration<T, P, R> searchConfiguration);

    /**
     * Returns all entities matching conditions applied from search request.
     *
     * @param request search request that contains query values
     * @param searchConfiguration configuration that decides how query should be built
     * @return all entities matching the given conditions applied from search request
     */
    <R, P> List<P> findAll(R request, SearchConfiguration<T, P, R> searchConfiguration);

    /**
     * Returns all entities matching conditions applied from search request sorted by sort parameter.
     *
     * @param request search request that contains query values
     * @param searchConfiguration configuration that decides how query should be built
     * @param sort the {@link Sort} specification to sort the results by, must not be {@literal null}.
     * @return all entities matching the given conditions applied from search request
     */
    <R, P> List<P> findAll(R request, SearchConfiguration<T, P, R> searchConfiguration, Sort sort);

    /**
     * Returns a {@link Page} of entities matching conditions applied from search request. In case no match could be found, an empty {@link Page} is returned.
     *
     * @param request search request that contains query values
     * @param searchConfiguration configuration that decides how query should be built
     * @param pageable can be {@literal null}.
     * @return a {@link Page} of entities matching the given conditions applied from search request
     */
    <R, P> Page<P> findAll(R request, SearchConfiguration<T, P, R> searchConfiguration, Pageable pageable);

    /**
     * Returns the number of instances matching conditions applied from search request.
     *
     * @param request search request that contains query values
     * @param searchConfiguration configuration that decides how query should be built
     * @return the number of instances matching conditions applied from search request.
     */
    <R, P> long count(R request, SearchConfiguration<T, P, R> searchConfiguration);

    /**
     * Whether the data store contains elements matching conditions applied from search request.
     *
     * @param request search request that contains query values
     * @param searchConfiguration configuration that decides how query should be built
     * @return {@literal true} if the data store contains elements matching conditions applied from search request.
     */
    <R, P> boolean exists(R request, SearchConfiguration<T, P, R> searchConfiguration);
}

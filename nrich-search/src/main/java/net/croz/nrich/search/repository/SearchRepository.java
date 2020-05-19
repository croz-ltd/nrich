package net.croz.nrich.search.repository;

import net.croz.nrich.search.model.SearchConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface SearchRepository<T> {

    <R, P> Optional<P> findOne(R request, SearchConfiguration<T, P, R> configuration);

    <R, P> long count(R request, SearchConfiguration<T, P, R> configuration);

    <R, P> List<P> findAll(R request, SearchConfiguration<T, P, R> configuration);

    <R, P> List<P> findAll(R request, SearchConfiguration<T, P, R> configuration, Sort sort);

    <R, P> Page<P> findAll(R request, SearchConfiguration<T, P, R> configuration, Pageable pageable);

}

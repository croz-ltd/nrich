package net.croz.nrich.search.repository;

import net.croz.nrich.search.model.SearchSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface SearchExecutor<T> {

    <R, P> Optional<P> findOne(R request, SearchSpecification<T, P, R> searchSpecification);

    <R, P> List<P> findAll(R request, SearchSpecification<T, P, R> searchSpecification);

    <R, P> List<P> findAll(R request, SearchSpecification<T, P, R> searchSpecification, Sort sort);

    <R, P> Page<P> findAll(R request, SearchSpecification<T, P, R> searchSpecification, Pageable pageable);

    <R, P> long count(R request, SearchSpecification<T, P, R> searchSpecification);

    <R, P> boolean exists(R request, SearchSpecification<T, P, R> searchSpecification);
}

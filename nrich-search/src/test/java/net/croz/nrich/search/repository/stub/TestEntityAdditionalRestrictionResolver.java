package net.croz.nrich.search.repository.stub;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.api.model.AdditionalRestrictionResolver;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class TestEntityAdditionalRestrictionResolver implements AdditionalRestrictionResolver<TestEntity, TestEntity, TestEntitySearchRequest> {

    private final boolean appendAdditionalRestriction;

    @Override
    public List<Predicate> resolvePredicateList(final CriteriaBuilder criteriaBuilder, final CriteriaQuery<TestEntity> query, final Root<TestEntity> root, final TestEntitySearchRequest request) {
        if (appendAdditionalRestriction) {
            return Collections.singletonList(criteriaBuilder.greaterThan(root.get("age"), 100));
        }

        return null;
    }
}

/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.search.repository;

import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.api.repository.StringSearchExecutor;
import net.croz.nrich.search.support.JpaQueryBuilder;
import net.croz.nrich.search.util.QueryUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.metamodel.ManagedType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional(readOnly = true)
public class JpaStringSearchExecutor<T> implements StringSearchExecutor<T> {

    private final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    private final EntityManager entityManager;

    private final JpaQueryBuilder<T> queryBuilder;

    private final ManagedType<?> managedType;

    public JpaStringSearchExecutor(StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter, EntityManager entityManager, JpaEntityInformation<T, ?> jpaEntityInformation) {
        this.stringToEntityPropertyMapConverter = stringToEntityPropertyMapConverter;
        this.entityManager = entityManager;
        this.queryBuilder = new JpaQueryBuilder<>(entityManager, jpaEntityInformation.getJavaType());
        this.managedType = jpaEntityInformation.getRequiredIdAttribute().getDeclaringType();
    }

    @Override
    public <P> Optional<P> findOne(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList, searchConfiguration);

        CriteriaQuery<P> query = queryBuilder.buildQuery(searchMap, searchConfiguration, Sort.unsorted());

        try {
            return Optional.of(entityManager.createQuery(query).getSingleResult());
        }
        catch (NoResultException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public <P> List<P> findAll(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList, searchConfiguration);

        CriteriaQuery<P> query = queryBuilder.buildQuery(searchMap, searchConfiguration, Sort.unsorted());

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public <P> List<P> findAll(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration, Sort sort) {
        Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList, searchConfiguration);

        CriteriaQuery<P> query = queryBuilder.buildQuery(searchMap, searchConfiguration, sort);

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public <P> Page<P> findAll(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration, Pageable pageable) {
        Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList, searchConfiguration);

        CriteriaQuery<P> query = queryBuilder.buildQuery(searchMap, searchConfiguration, pageable.getSort());
        TypedQuery<P> typedQuery = entityManager.createQuery(query);

        if (pageable.isPaged()) {
            typedQuery.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());

            return PageableExecutionUtils.getPage(typedQuery.getResultList(), pageable, () -> executeCountQuery(searchMap, searchConfiguration));
        }

        return new PageImpl<>(typedQuery.getResultList());
    }

    @Override
    public <P> long count(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList, searchConfiguration);

        return executeCountQuery(searchMap, searchConfiguration);
    }

    @Override
    public <P> boolean exists(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        Map<String, Object> searchMap = convertToMap(searchTerm, propertyToSearchList, searchConfiguration);

        CriteriaQuery<Integer> query = queryBuilder.buildExistsQuery(searchMap, searchConfiguration);

        return entityManager.createQuery(query).setMaxResults(1).getResultList().size() == 1;
    }

    private Map<String, Object> convertToMap(String searchTerm, List<String> propertyToSearchList, SearchConfiguration<T, ?, Map<String, Object>> searchConfiguration) {
        return stringToEntityPropertyMapConverter.convert(searchTerm, propertyToSearchList, managedType, searchConfiguration.getSearchPropertyConfiguration());
    }

    private <P> long executeCountQuery(Map<String, Object> searchMap, SearchConfiguration<T, P, Map<String, Object>> searchConfiguration) {
        CriteriaQuery<Long> countQuery = queryBuilder.buildCountQuery(searchMap, searchConfiguration);

        List<Long> totals = entityManager.createQuery(countQuery).getResultList();

        return QueryUtil.toCountResult(totals);
    }
}

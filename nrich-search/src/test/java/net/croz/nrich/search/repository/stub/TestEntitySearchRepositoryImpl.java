package net.croz.nrich.search.repository.stub;

import net.croz.nrich.search.properties.SearchProperties;
import net.croz.nrich.search.repository.impl.SearchRepositoryImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class TestEntitySearchRepositoryImpl extends SearchRepositoryImpl<TestEntity, TestEntitySearchRequest> {

    public TestEntitySearchRepositoryImpl(final EntityManager entityManager, final SearchProperties searchProperties) {
        super(entityManager, searchProperties);
    }
}

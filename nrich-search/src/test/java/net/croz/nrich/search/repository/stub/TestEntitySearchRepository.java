package net.croz.nrich.search.repository.stub;

import net.croz.nrich.search.properties.SearchProperties;
import net.croz.nrich.search.repository.SearchRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class TestEntitySearchRepository extends SearchRepository<TestEntity, TestEntitySearchRequest> {

    public TestEntitySearchRepository(final EntityManager entityManager, final SearchProperties searchProperties) {
        super(entityManager, searchProperties);
    }
}

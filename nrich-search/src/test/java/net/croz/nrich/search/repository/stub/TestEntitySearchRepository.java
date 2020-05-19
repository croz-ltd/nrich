package net.croz.nrich.search.repository.stub;

import net.croz.nrich.search.repository.SearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestEntitySearchRepository extends JpaRepository<TestEntity, Long>, SearchRepository<TestEntity> {

}

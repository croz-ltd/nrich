package net.croz.nrich.search.repository.stub;

import net.croz.nrich.search.repository.StringSearchExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestEntityStringSearchRepository extends JpaRepository<TestStringSearchEntity, Long>, StringSearchExecutor<TestStringSearchEntity> {

}
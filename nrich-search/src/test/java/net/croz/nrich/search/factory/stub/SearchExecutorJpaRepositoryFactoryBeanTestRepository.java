package net.croz.nrich.search.factory.stub;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchExecutorJpaRepositoryFactoryBeanTestRepository extends JpaRepository<SearchExecutorJpaRepositoryFactoryBeanTestEntity, Long> {

}

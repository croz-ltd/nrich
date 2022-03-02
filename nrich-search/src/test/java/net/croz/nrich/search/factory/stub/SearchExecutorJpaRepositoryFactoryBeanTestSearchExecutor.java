package net.croz.nrich.search.factory.stub;

import net.croz.nrich.search.api.repository.SearchExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchExecutorJpaRepositoryFactoryBeanTestSearchExecutor extends JpaRepository<SearchExecutorJpaRepositoryFactoryBeanTestEntity, Long>, SearchExecutor<SearchExecutorJpaRepositoryFactoryBeanTestEntity> {

}

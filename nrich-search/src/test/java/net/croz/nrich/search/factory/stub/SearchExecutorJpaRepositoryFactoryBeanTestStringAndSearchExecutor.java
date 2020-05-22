package net.croz.nrich.search.factory.stub;

import net.croz.nrich.search.repository.SearchExecutor;
import net.croz.nrich.search.repository.StringSearchExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchExecutorJpaRepositoryFactoryBeanTestStringAndSearchExecutor extends JpaRepository<SearchExecutorJpaRepositoryFactoryBeanTestEntity, Long>, SearchExecutor<SearchExecutorJpaRepositoryFactoryBeanTestEntity>, StringSearchExecutor<SearchExecutorJpaRepositoryFactoryBeanTestEntity> {
}

package net.croz.nrich.search.factory.stub;

import net.croz.nrich.search.api.repository.StringSearchExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchExecutorJpaRepositoryFactoryBeanTestStringSearchExecutor extends JpaRepository<SearchExecutorJpaRepositoryFactoryBeanTestEntity, Long>, StringSearchExecutor<SearchExecutorJpaRepositoryFactoryBeanTestEntity> {
}

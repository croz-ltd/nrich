package net.croz.nrich.search.factory.stub;

import net.croz.nrich.search.repository.SearchExecutor;
import net.croz.nrich.search.repository.StringSearchExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepositoryJpaRepositoryFactoryBeanTestStringAndSearchExecutor extends JpaRepository<SearchRepositoryJpaRepositoryFactoryBeanTestEntity, Long>, SearchExecutor<SearchRepositoryJpaRepositoryFactoryBeanTestEntity>, StringSearchExecutor<SearchRepositoryJpaRepositoryFactoryBeanTestEntity> {
}

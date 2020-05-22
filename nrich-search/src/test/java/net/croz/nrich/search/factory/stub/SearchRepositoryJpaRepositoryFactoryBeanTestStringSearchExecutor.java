package net.croz.nrich.search.factory.stub;

import net.croz.nrich.search.repository.StringSearchExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepositoryJpaRepositoryFactoryBeanTestStringSearchExecutor extends JpaRepository<SearchRepositoryJpaRepositoryFactoryBeanTestEntity, Long>, StringSearchExecutor<SearchRepositoryJpaRepositoryFactoryBeanTestEntity> {
}

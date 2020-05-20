package net.croz.nrich.search.factory.stub;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepositoryJpaRepositoryFactoryBeanTestRepository extends JpaRepository<SearchRepositoryJpaRepositoryFactoryBeanTestEntity, Long> {

}

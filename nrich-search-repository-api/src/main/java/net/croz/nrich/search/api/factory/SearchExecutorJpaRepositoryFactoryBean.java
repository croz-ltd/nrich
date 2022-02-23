package net.croz.nrich.search.api.factory;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

/**
 * Factory that supports creating repository factories for {@link net.croz.nrich.search.api.repository.SearchExecutor} and {@link net.croz.nrich.search.api.repository.StringSearchExecutor} interfaces.
 *
 * @param <T>  repository type
 * @param <S>  entity type
 * @param <I> id type
 */
public class SearchExecutorJpaRepositoryFactoryBean<T extends Repository<S, I>, S, I> extends JpaRepositoryFactoryBean<T, S, I> {

    private final RepositoryFactorySupportFactory repositoryFactorySupportFactory;

    private final Class<? extends T> repositoryInterface;

    public SearchExecutorJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface, RepositoryFactorySupportFactory repositoryFactorySupportFactory) {
        super(repositoryInterface);
        this.repositoryInterface = repositoryInterface;
        this.repositoryFactorySupportFactory = repositoryFactorySupportFactory;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return repositoryFactorySupportFactory.createRepositoryFactory(repositoryInterface, entityManager);
    }

}

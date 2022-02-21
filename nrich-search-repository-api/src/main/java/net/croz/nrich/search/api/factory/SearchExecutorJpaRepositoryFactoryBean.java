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
 * @param <ID> id type
 */
public class SearchExecutorJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends JpaRepositoryFactoryBean<T, S, ID> {

    private final RepositoryFactorySupportFactory repositoryFactorySupportFactory;

    private final Class<? extends T> repositoryInterface;

    public SearchExecutorJpaRepositoryFactoryBean(final Class<? extends T> repositoryInterface, final RepositoryFactorySupportFactory repositoryFactorySupportFactory) {
        super(repositoryInterface);
        this.repositoryInterface = repositoryInterface;
        this.repositoryFactorySupportFactory = repositoryFactorySupportFactory;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(final EntityManager entityManager) {
        return repositoryFactorySupportFactory.createRepositoryFactory(repositoryInterface, entityManager);
    }

}

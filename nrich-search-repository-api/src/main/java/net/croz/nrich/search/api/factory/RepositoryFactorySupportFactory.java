package net.croz.nrich.search.api.factory;

import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

/**
 * Enables creation of {@link RepositoryFactorySupport} instances for custom repository implementations.
 */
public interface RepositoryFactorySupportFactory {

    /**
     * Returns {@link RepositoryFactorySupport} instance for repository interface.
     *
     * @param repositoryInterface repository interface for which to return implementation of {@link RepositoryFactorySupport}
     * @param entityManager entity manager
     * @return {@link RepositoryFactorySupport} instance for repository interface
     */
    RepositoryFactorySupport createRepositoryFactory(Class<?> repositoryInterface, EntityManager entityManager);

}

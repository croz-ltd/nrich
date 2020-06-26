package net.croz.nrich.search.factory;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.factory.RepositoryFactorySupportFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
public class SearchRepositoryFactorySupportFactory implements RepositoryFactorySupportFactory {

    private final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    @Override
    public RepositoryFactorySupport createRepositoryFactory(final Class<?> repositoryInterface, final EntityManager entityManager) {
        return new SearchRepositoryJpaRepositoryFactory(entityManager, stringToEntityPropertyMapConverter);
    }
}

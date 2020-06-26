package net.croz.nrich.search.factory;

import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.factory.RepositoryFactorySupportFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

public class SearchRepositoryFactorySupportFactory implements RepositoryFactorySupportFactory {

    @Override
    public RepositoryFactorySupport createRepositoryFactory(final EntityManager entityManager, final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter) {
        return new SearchRepositoryJpaRepositoryFactory(entityManager, stringToEntityPropertyMapConverter);
    }
}

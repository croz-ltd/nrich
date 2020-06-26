package net.croz.nrich.search.api.factory;

import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

public interface RepositoryFactorySupportFactory {

    RepositoryFactorySupport createRepositoryFactory(Class<?> repositoryInterface, EntityManager entityManager, StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter);

}

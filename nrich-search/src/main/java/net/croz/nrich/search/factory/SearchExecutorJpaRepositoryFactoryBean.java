package net.croz.nrich.search.factory;

import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

public class SearchExecutorJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends JpaRepositoryFactoryBean<T, S, ID> {

    private final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    public SearchExecutorJpaRepositoryFactoryBean(final Class<? extends T> repositoryInterface, final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter) {
        super(repositoryInterface);
        this.stringToEntityPropertyMapConverter = stringToEntityPropertyMapConverter;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(final EntityManager entityManager) {
        return new SearchRepositoryJpaRepositoryFactory(entityManager, stringToEntityPropertyMapConverter);
    }

}

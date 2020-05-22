package net.croz.nrich.search.factory;

import net.croz.nrich.search.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.repository.SearchExecutor;
import net.croz.nrich.search.repository.StringSearchExecutor;
import net.croz.nrich.search.repository.impl.JpaSearchExecutor;
import net.croz.nrich.search.repository.impl.JpaStringSearchExecutor;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryFragment;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class SearchRepositoryJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends JpaRepositoryFactoryBean<T, S, ID> {

    private final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    public SearchRepositoryJpaRepositoryFactoryBean(final Class<? extends T> repositoryInterface, final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter) {
        super(repositoryInterface);
        this.stringToEntityPropertyMapConverter = stringToEntityPropertyMapConverter;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(final EntityManager entityManager) {
        return new SearchRepositoryJpaRepositoryFactory(entityManager, stringToEntityPropertyMapConverter);
    }

    private static class SearchRepositoryJpaRepositoryFactory extends JpaRepositoryFactory {

        private final EntityManager entityManager;

        private final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

        public SearchRepositoryJpaRepositoryFactory(final EntityManager entityManager, final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter) {
            super(entityManager);
            this.entityManager = entityManager;
            this.stringToEntityPropertyMapConverter = stringToEntityPropertyMapConverter;
        }

        @Override
        protected RepositoryComposition.RepositoryFragments getRepositoryFragments(final RepositoryMetadata metadata) {
            RepositoryComposition.RepositoryFragments fragments = super.getRepositoryFragments(metadata);

            SearchExecutor<?> searchExecutorFragment = null;
            if (SearchExecutor.class.isAssignableFrom(metadata.getRepositoryInterface())) {
                searchExecutorFragment = getTargetRepositoryViaReflection(JpaSearchExecutor.class, entityManager, getEntityInformation(metadata.getDomainType()));

                fragments = fragments.append(RepositoryFragment.implemented(SearchExecutor.class, searchExecutorFragment));
            }
            if (StringSearchExecutor.class.isAssignableFrom(metadata.getRepositoryInterface())) {
                final JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

                if (searchExecutorFragment == null) {
                    searchExecutorFragment = getTargetRepositoryViaReflection(JpaSearchExecutor.class, entityManager, entityInformation);
                }

                final StringSearchExecutor<?> stringSearchExecutorFragment = getTargetRepositoryViaReflection(JpaStringSearchExecutor.class, stringToEntityPropertyMapConverter, searchExecutorFragment, entityInformation);

                fragments = fragments.append(RepositoryFragment.implemented(StringSearchExecutor.class, stringSearchExecutorFragment));
            }

            return fragments;
        }
    }
}

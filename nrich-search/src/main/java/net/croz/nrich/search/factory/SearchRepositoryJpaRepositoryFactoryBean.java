package net.croz.nrich.search.factory;

import net.croz.nrich.search.properties.SearchProperties;
import net.croz.nrich.search.repository.SearchRepository;
import net.croz.nrich.search.repository.impl.SearchRepositoryExecutor;
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

    private final SearchProperties searchProperties;

    public SearchRepositoryJpaRepositoryFactoryBean(final Class<? extends T> repositoryInterface, final SearchProperties searchProperties) {
        super(repositoryInterface);
        this.searchProperties = searchProperties;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(final EntityManager entityManager) {
        return new SearchRepositoryJpaRepositoryFactory(entityManager, searchProperties);
    }

    private static class SearchRepositoryJpaRepositoryFactory extends JpaRepositoryFactory {

        private final EntityManager entityManager;

        private final SearchProperties searchProperties;

        public SearchRepositoryJpaRepositoryFactory(final EntityManager entityManager, final SearchProperties searchProperties) {
            super(entityManager);
            this.entityManager = entityManager;
            this.searchProperties = searchProperties;
        }

        @Override
        protected RepositoryComposition.RepositoryFragments getRepositoryFragments(final RepositoryMetadata metadata) {
            RepositoryComposition.RepositoryFragments fragments = super.getRepositoryFragments(metadata);

            if (SearchRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {
                final JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

                final SearchRepository<?> searchRepositoryFragment = getTargetRepositoryViaReflection(SearchRepositoryExecutor.class, entityManager, searchProperties, entityInformation);

                fragments = fragments.append(RepositoryFragment.implemented(SearchRepository.class, searchRepositoryFragment));
            }

            return fragments;
        }
    }
}

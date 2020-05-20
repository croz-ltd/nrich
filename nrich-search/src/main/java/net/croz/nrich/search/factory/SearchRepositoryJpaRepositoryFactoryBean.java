package net.croz.nrich.search.factory;

import net.croz.nrich.search.repository.SearchExecutor;
import net.croz.nrich.search.repository.impl.JpaSearchRepositoryExecutor;
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

    public SearchRepositoryJpaRepositoryFactoryBean(final Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(final EntityManager entityManager) {
        return new SearchRepositoryJpaRepositoryFactory(entityManager);
    }

    private static class SearchRepositoryJpaRepositoryFactory extends JpaRepositoryFactory {

        private final EntityManager entityManager;

        public SearchRepositoryJpaRepositoryFactory(final EntityManager entityManager) {
            super(entityManager);
            this.entityManager = entityManager;
        }

        @Override
        protected RepositoryComposition.RepositoryFragments getRepositoryFragments(final RepositoryMetadata metadata) {
            RepositoryComposition.RepositoryFragments fragments = super.getRepositoryFragments(metadata);

            if (SearchExecutor.class.isAssignableFrom(metadata.getRepositoryInterface())) {
                final JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

                final SearchExecutor<?> searchExecutorFragment = getTargetRepositoryViaReflection(JpaSearchRepositoryExecutor.class, entityManager, entityInformation);

                fragments = fragments.append(RepositoryFragment.implemented(SearchExecutor.class, searchExecutorFragment));
            }

            return fragments;
        }
    }
}

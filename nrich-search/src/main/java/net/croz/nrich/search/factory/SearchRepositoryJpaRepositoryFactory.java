package net.croz.nrich.search.factory;

import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.repository.SearchExecutor;
import net.croz.nrich.search.api.repository.StringSearchExecutor;
import net.croz.nrich.search.repository.JpaSearchExecutor;
import net.croz.nrich.search.repository.JpaStringSearchExecutor;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class SearchRepositoryJpaRepositoryFactory extends JpaRepositoryFactory {

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

        if (SearchExecutor.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            final SearchExecutor<?> searchExecutorFragment = getTargetRepositoryViaReflection(JpaSearchExecutor.class, entityManager, getEntityInformation(metadata.getDomainType()));

            fragments = fragments.append(RepositoryFragment.implemented(SearchExecutor.class, searchExecutorFragment));
        }
        if (StringSearchExecutor.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            final JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

            final StringSearchExecutor<?> stringSearchExecutorFragment = getTargetRepositoryViaReflection(JpaStringSearchExecutor.class, stringToEntityPropertyMapConverter, entityManager, entityInformation);

            fragments = fragments.append(RepositoryFragment.implemented(StringSearchExecutor.class, stringSearchExecutorFragment));
        }

        return fragments;
    }
}

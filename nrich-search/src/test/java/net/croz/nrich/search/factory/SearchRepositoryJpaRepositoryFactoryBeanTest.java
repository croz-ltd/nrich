package net.croz.nrich.search.factory;

import net.croz.nrich.search.SearchConfigurationTestConfiguration;
import net.croz.nrich.search.factory.stub.SearchRepositoryJpaRepositoryFactoryBeanTestRepository;
import net.croz.nrich.search.factory.stub.SearchRepositoryJpaRepositoryFactoryBeanTestSearchExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = SearchConfigurationTestConfiguration.class)
public class SearchRepositoryJpaRepositoryFactoryBeanTest {

    @Autowired
    private SearchRepositoryJpaRepositoryFactoryBeanTestRepository searchRepositoryJpaRepositoryFactoryBeanTestRepository;

    @Autowired
    private SearchRepositoryJpaRepositoryFactoryBeanTestSearchExecutor searchRepositoryJpaRepositoryFactoryBeanTestSearchExecutor;

    @Test
    void shouldInitializeRegularRepositoryBean() {
        // expect
        assertThat(searchRepositoryJpaRepositoryFactoryBeanTestRepository).isNotNull();
    }

    @Test
    void shouldInitializeSearchExecutorRepository() {
        // expect
        assertThat(searchRepositoryJpaRepositoryFactoryBeanTestSearchExecutor).isNotNull();
    }
}

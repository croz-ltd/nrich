package net.croz.nrich.search.factory;

import net.croz.nrich.search.SearchTestConfiguration;
import net.croz.nrich.search.factory.stub.SearchExecutorJpaRepositoryFactoryBeanTestRepository;
import net.croz.nrich.search.factory.stub.SearchExecutorJpaRepositoryFactoryBeanTestSearchExecutor;
import net.croz.nrich.search.factory.stub.SearchExecutorJpaRepositoryFactoryBeanTestStringAndSearchExecutor;
import net.croz.nrich.search.factory.stub.SearchExecutorJpaRepositoryFactoryBeanTestStringSearchExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = SearchTestConfiguration.class)
public class SearchExecutorJpaRepositoryFactoryBeanTest {

    @Autowired
    private SearchExecutorJpaRepositoryFactoryBeanTestRepository searchExecutorJpaRepositoryFactoryBeanTestRepository;

    @Autowired
    private SearchExecutorJpaRepositoryFactoryBeanTestSearchExecutor searchExecutorJpaRepositoryFactoryBeanTestSearchExecutor;

    @Autowired
    private SearchExecutorJpaRepositoryFactoryBeanTestStringSearchExecutor searchExecutorJpaRepositoryFactoryBeanTestStringSearchExecutor;

    @Autowired
    private SearchExecutorJpaRepositoryFactoryBeanTestStringAndSearchExecutor searchExecutorJpaRepositoryFactoryBeanTestStringAndSearchExecutor;

    @Test
    void shouldInitializeRegularRepositoryBean() {
        // expect
        assertThat(searchExecutorJpaRepositoryFactoryBeanTestRepository).isNotNull();
    }

    @Test
    void shouldInitializeSearchExecutorRepository() {
        // expect
        assertThat(searchExecutorJpaRepositoryFactoryBeanTestSearchExecutor).isNotNull();
    }

    @Test
    void shouldInitializeStringSearchExecutorRepository() {
        // expect
        assertThat(searchExecutorJpaRepositoryFactoryBeanTestStringSearchExecutor).isNotNull();
    }

    @Test
    void shouldInitializeBeanThatImplementsBothInterfacesCorrectly() {
        // expect
        assertThat(searchExecutorJpaRepositoryFactoryBeanTestStringAndSearchExecutor).isNotNull();
    }
}

package net.croz.nrich.registry.data.service.impl;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryListRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityList;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringJUnitConfig(RegistryTestConfiguration.class)
public class RegistryDataServiceImplTest {

    @Autowired
    private RegistryDataServiceImpl registryDataService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldSearchRegistry() {
        // given
        createRegistryTestEntityList(entityManager);

        final ListRegistryRequest request = createRegistryListRequest(RegistryTestEntity.class.getName(), "name%");

        // when
        final Page<RegistryTestEntity> result = registryDataService.registryList(request);

        // then
        assertThat(result).hasSize(5);
    }
}

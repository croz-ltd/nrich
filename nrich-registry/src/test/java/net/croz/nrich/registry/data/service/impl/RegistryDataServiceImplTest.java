package net.croz.nrich.registry.data.service.impl;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.data.request.BulkListRegistryRequest;
import net.croz.nrich.registry.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryServiceRequest;
import net.croz.nrich.registry.data.service.RegistryDataService;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Map;

import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createBulkListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createDeleteRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryServiceRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntity;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.updateRegistryServiceRequest;
import static net.croz.nrich.registry.testutil.PersistenceTestUtil.flushEntityManager;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringJUnitWebConfig(RegistryTestConfiguration.class)
public class RegistryDataServiceImplTest {

    @Autowired
    private RegistryDataService registryDataService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldBulkListRegistry() {
        // given
        createRegistryTestEntityList(entityManager);

        final BulkListRegistryRequest request = createBulkListRegistryRequest(RegistryTestEntity.class.getName(), "name%");

        // when
        final Map<String, Page<RegistryTestEntity>> result = registryDataService.bulkList(request);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(RegistryTestEntity.class.getName())).hasSize(5);
    }

    @Test
    void shouldListRegistry() {
        // given
        createRegistryTestEntityList(entityManager);

        final ListRegistryRequest request = createListRegistryRequest(RegistryTestEntity.class.getName(), "name%");

        // when
        final Page<RegistryTestEntity> result = registryDataService.list(request);

        // then
        assertThat(result).hasSize(5);
    }

    @Test
    void shouldReturnAllElementsOnEmptySearchParameters() {
        // given
        createRegistryTestEntityList(entityManager);

        final ListRegistryRequest request = createListRegistryRequest(RegistryTestEntity.class.getName(), null);

        // when
        final Page<RegistryTestEntity> result = registryDataService.list(request);

        // then
        assertThat(result).hasSize(5);
    }

    @Test
    void shouldCreateRegistryEntity() {
        // given
        final CreateRegistryServiceRequest request = createRegistryServiceRequest(RegistryTestEntity.class.getName());

        // when
        final RegistryTestEntity registryTestEntity = registryDataService.create(request);

        // then
        assertThat(registryTestEntity).isNotNull();

        // and when
        flushEntityManager(entityManager);
        final RegistryTestEntity loaded = entityManager.find(RegistryTestEntity.class, registryTestEntity.getId());

        // then
        assertThat(loaded.getAge()).isEqualTo(50);
        assertThat(loaded.getName()).isEqualTo("name 1");
    }

    @Test
    void shouldUpdateRegistryEntity() {
        // given
        final RegistryTestEntity registryTestEntity = createRegistryTestEntity(entityManager);

        final UpdateRegistryServiceRequest request = updateRegistryServiceRequest(RegistryTestEntity.class.getName(), registryTestEntity.getId());

        // when
        final RegistryTestEntity updatedEntity = registryDataService.update(request);

        // then
        assertThat(updatedEntity).isNotNull();

        // and when
        flushEntityManager(entityManager);
        final RegistryTestEntity loaded = entityManager.find(RegistryTestEntity.class, registryTestEntity.getId());

        // then
        assertThat(loaded.getAge()).isEqualTo(51);
        assertThat(loaded.getName()).isEqualTo("name 2");
    }

    @Test
    void shouldDeleteRegistryEntity() {
        // given
        final RegistryTestEntity registryTestEntity = createRegistryTestEntity(entityManager);

        final DeleteRegistryRequest request = createDeleteRegistryRequest(RegistryTestEntity.class.getName(), registryTestEntity.getId());

        // when
        final boolean result = registryDataService.delete(request);

        // then
        assertThat(result).isTrue();
    }
}

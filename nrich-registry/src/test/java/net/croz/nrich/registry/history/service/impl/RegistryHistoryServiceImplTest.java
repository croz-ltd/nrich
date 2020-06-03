package net.croz.nrich.registry.history.service.impl;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.history.model.EntityWithRevision;
import net.croz.nrich.registry.history.request.ListRegistryHistoryRequest;
import net.croz.nrich.registry.history.service.RegistryHistoryService;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static net.croz.nrich.registry.history.testutil.RegistryHistoryGeneratingUtil.creteRevisionList;
import static net.croz.nrich.registry.history.testutil.RegistryHistoryGeneratingUtil.listRegistryHistoryRequest;
import static net.croz.nrich.registry.testutil.PersistenceTestUtil.executeInTransaction;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitWebConfig(RegistryTestConfiguration.class)
public class RegistryHistoryServiceImplTest {

    @Autowired
    private RegistryHistoryService registryHistoryService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Test
    void shouldReturnAllRevisionsOfEntity() {
        // given
        final RegistryHistoryTestEntity entity = executeInTransaction(platformTransactionManager, () -> creteRevisionList(entityManager));
        final ListRegistryHistoryRequest request = listRegistryHistoryRequest(RegistryHistoryTestEntity.class.getName(), entity.getId());

        // when
        List<EntityWithRevision<RegistryHistoryTestEntity>> result = registryHistoryService.historyList(request);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).extracting("entity.name").containsExactlyInAnyOrder("new name");
    }
}

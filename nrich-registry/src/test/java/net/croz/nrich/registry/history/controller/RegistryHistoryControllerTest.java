package net.croz.nrich.registry.history.controller;

import net.croz.nrich.registry.api.history.request.ListRegistryHistoryRequest;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntity;
import net.croz.nrich.registry.test.BaseControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static net.croz.nrich.registry.history.testutil.RegistryHistoryGeneratingUtil.creteRegistryHistoryTestEntityRevisionList;
import static net.croz.nrich.registry.history.testutil.RegistryHistoryGeneratingUtil.listRegistryHistoryRequest;
import static net.croz.nrich.registry.testutil.PersistenceTestUtil.executeInTransaction;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegistryHistoryControllerTest extends BaseControllerTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Test
    void shouldFetchRegistryHistoryList() throws Exception {
        // given
        String requestUrl = "/nrich/registry/history/list";
        RegistryHistoryTestEntity entity = creteRegistryHistoryTestEntityRevisionList(entityManager, platformTransactionManager);
        ListRegistryHistoryRequest request = listRegistryHistoryRequest(RegistryHistoryTestEntity.class.getName(), entity.getId());

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.numberOfElements").value(10));
    }

    @AfterEach
    void cleanup() {
        executeInTransaction(platformTransactionManager, () -> entityManager.createQuery("delete from " + RegistryHistoryTestEntity.class.getName()).executeUpdate());
        executeInTransaction(platformTransactionManager, () -> entityManager.createQuery("delete from " + RegistryHistoryTestEntity.class.getName() + "_AUD").executeUpdate());
    }
}

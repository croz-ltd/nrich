package net.croz.nrich.registry.history.controller;

import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.registry.api.history.model.EntityWithRevision;
import net.croz.nrich.registry.api.history.request.ListRegistryHistoryRequest;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntity;
import net.croz.nrich.registry.test.BaseWebTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static net.croz.nrich.registry.history.testutil.RegistryHistoryGeneratingUtil.creteRegistryHistoryTestEntityRevisionList;
import static net.croz.nrich.registry.history.testutil.RegistryHistoryGeneratingUtil.listRegistryHistoryRequest;
import static net.croz.nrich.registry.testutil.PersistenceTestUtil.executeInTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class RegistryHistoryControllerTest extends BaseWebTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Test
    void shouldFetchRegistryHistoryList() throws Exception {
        // given
        final RegistryHistoryTestEntity entity = creteRegistryHistoryTestEntityRevisionList(entityManager, platformTransactionManager);
        final ListRegistryHistoryRequest request = listRegistryHistoryRequest(RegistryHistoryTestEntity.class.getName(), entity.getId());

        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrich/registry/history/list").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // and when
        final PageHolder convertedResponse = objectMapper.readValue(response.getContentAsString(), PageHolder.class);

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse.getContent()).hasSize(10);
    }

    @AfterEach
    void cleanup() {
        executeInTransaction(platformTransactionManager, () -> entityManager.createQuery("delete from " + RegistryHistoryTestEntity.class.getName()).executeUpdate());
        executeInTransaction(platformTransactionManager, () -> entityManager.createQuery("delete from " + RegistryHistoryTestEntity.class.getName()  + "_AUD").executeUpdate());
    }

    @Setter
    @Getter
    static class PageHolder {

        private List<EntityWithRevision<RegistryHistoryTestEntity>> content;

    }
}

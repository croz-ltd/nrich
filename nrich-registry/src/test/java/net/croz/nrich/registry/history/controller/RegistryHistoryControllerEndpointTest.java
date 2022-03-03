package net.croz.nrich.registry.history.controller;

import net.croz.nrich.registry.api.history.request.ListRegistryHistoryRequest;
import net.croz.nrich.registry.history.stub.RegistryHistoryTestEntity;
import net.croz.nrich.registry.test.BaseWebTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;

import static net.croz.nrich.registry.history.testutil.RegistryHistoryGeneratingUtil.listRegistryHistoryRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@TestPropertySource(locations = "/application-test.yml")
class RegistryHistoryControllerEndpointTest extends BaseWebTest {

    @Test
    void shouldFetchRegistryHistoryList() throws Exception {
        // given
        ListRegistryHistoryRequest request = listRegistryHistoryRequest(RegistryHistoryTestEntity.class.getName(), 1L);

        // when
        MockHttpServletResponse response = mockMvc.perform(post("/domain/nrich/registry/history/list")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}

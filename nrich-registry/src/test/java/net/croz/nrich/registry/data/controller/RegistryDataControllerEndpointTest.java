package net.croz.nrich.registry.data.controller;

import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.test.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;

import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createListRegistryRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "nrich.registry.data.endpoint-path=api/registry/data")
class RegistryDataControllerEndpointTest extends BaseControllerTest {

    @Test
    void shouldListRegistryOnCustomDomain() throws Exception {
        // given
        String requestUrl = "/api/registry/data/list";
        ListRegistryRequest request = createListRegistryRequest(RegistryTestEntity.class.getName(), "name%");

        // when
        ResultActions result = performPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk());
    }
}

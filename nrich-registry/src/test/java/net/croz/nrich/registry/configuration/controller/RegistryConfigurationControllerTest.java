package net.croz.nrich.registry.configuration.controller;

import net.croz.nrich.registry.test.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegistryConfigurationControllerTest extends BaseControllerTest {

    private static final String REQUEST_URL = "/nrich/registry/configuration/fetch";

    @Test
    void shouldFetchRegistryConfiguration() throws Exception {
        // when
        ResultActions result = performPostRequest(REQUEST_URL);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void shouldSerializeJavascriptTypeAsLowerCase() throws Exception {
        // when
        ResultActions result = performPostRequest(REQUEST_URL);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("[*]['entityConfigurationList'][*]['propertyConfigurationList'][*]['javascriptType']",
                hasItems("string", "number", "object")));
    }
}

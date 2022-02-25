package net.croz.nrich.security.csrf.core.controller;

import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class CsrfPingControllerTest {

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(CsrfPingController.class).build();

    @Test
    void shouldReturnPingRequest() throws Exception {
        // when
        MockHttpServletResponse response = mockMvc.perform(post(CsrfConstants.CSRF_DEFAULT_PING_URI).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

}

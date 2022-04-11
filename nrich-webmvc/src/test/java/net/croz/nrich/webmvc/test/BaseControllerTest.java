package net.croz.nrich.webmvc.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.webmvc.WebmvcTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringJUnitWebConfig(WebmvcTestConfiguration.class)
public abstract class BaseControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    protected ResultActions performPostRequest(String requestUrl) throws Exception {
        return performPostRequest(requestUrl, Collections.emptyMap());
    }

    protected ResultActions performPostRequest(String requestUrl, Object requestData) throws Exception {
        return mockMvc.perform(post(requestUrl)
            .content(objectMapper.writeValueAsString(requestData))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performFormPostRequest(String requestUrl) throws Exception {
        return performFormPostRequest(requestUrl, Collections.emptyMap());
    }

    protected ResultActions performFormPostRequest(String requestUrl, Map<String, String> requestData) throws Exception {
        return mockMvc.perform(post(requestUrl)
            .params(createFromMap(requestData))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON));
    }

    private MultiValueMap<String, String> createFromMap(Map<String, String> requestData) {
        MultiValueMap<String, String> resultMap = new LinkedMultiValueMap<>();

        requestData.forEach((key, value) -> resultMap.put(key, Collections.singletonList(value)));

        return resultMap;
    }
}

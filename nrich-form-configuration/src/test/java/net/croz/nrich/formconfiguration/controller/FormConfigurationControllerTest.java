package net.croz.nrich.formconfiguration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.formconfiguration.api.model.FormConfiguration;
import net.croz.nrich.formconfiguration.api.request.FetchFormConfigurationRequest;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static net.croz.nrich.formconfiguration.testutil.FormConfigurationGeneratingUtil.createFetchFormConfigurationRequest;
import static net.croz.nrich.formconfiguration.testutil.FormConfigurationGeneratingUtil.createFormConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class FormConfigurationControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FormConfigurationService formConfigurationService;

    @InjectMocks
    private FormConfigurationController formConfigurationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(formConfigurationController).build();
    }

    @Test
    void shouldReturnFormConfiguration() throws Exception {
        // given
        FetchFormConfigurationRequest request = createFetchFormConfigurationRequest();
        FormConfiguration formConfiguration = createFormConfiguration();

        doReturn(Collections.singletonList(formConfiguration)).when(formConfigurationService).fetchFormConfigurationList(request.getFormIdList());

        // when
        MockHttpServletResponse response = mockMvc.perform(post("/nrich/form/configuration/fetch")
            .content(objectMapper.writeValueAsString(request)).accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // and when
        List<FormConfiguration> result = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<FormConfiguration>>() {
        });

        // then
        assertThat(result).extracting("formId").containsExactly(formConfiguration.getFormId());
    }
}

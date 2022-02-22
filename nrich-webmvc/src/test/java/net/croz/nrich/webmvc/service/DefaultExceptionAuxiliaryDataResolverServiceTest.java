package net.croz.nrich.webmvc.service;

import net.croz.nrich.webmvc.WebmvcTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitWebConfig(WebmvcTestConfiguration.class)
class DefaultExceptionAuxiliaryDataResolverServiceTest {

    @Autowired
    private DefaultExceptionAuxiliaryDataResolverService exceptionAuxiliaryDataResolverService;

    @Test
    void shouldResolveExceptionAuxiliaryData() {
        // given
        RuntimeException exception = new RuntimeException("Exception");
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("POST", "uri");

        // when
        Map<String, Object> additionalExceptionData = exceptionAuxiliaryDataResolverService.resolveRequestExceptionAuxiliaryData(exception, mockHttpServletRequest);

        // then
        assertThat(additionalExceptionData).isNotNull();
        assertThat(additionalExceptionData.get("uuid")).isNotNull();
        assertThat(additionalExceptionData.get("occurrenceTime")).isNotNull();
        assertThat(additionalExceptionData).containsEntry("requestUri", "uri").containsEntry("requestMethod", "POST");
    }
}

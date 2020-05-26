package net.croz.nrich.webmvc.service.impl;

import net.croz.nrich.webmvc.WebmvcTestConfiguration;
import net.croz.nrich.webmvc.service.stub.TransientPropertyResolverServiceImplTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@WebAppConfiguration
@SpringJUnitConfig(WebmvcTestConfiguration.class)
public class TransientPropertyResolverServiceImplTest {

    @Autowired
    private TransientPropertyResolverServiceImpl transientPropertyResolverService;

    @Test
    void shouldResolveTransientPropertyList() {
        // given
        final Class<?> type = TransientPropertyResolverServiceImplTestRequest.class;

        // when
        final List<String> resultList = transientPropertyResolverService.resolveTransientPropertyList(type);

        // then
        assertThat(resultList).containsExactlyInAnyOrder("value", "anotherValue");

        // and when
        final List<String> cachedResultList = transientPropertyResolverService.resolveTransientPropertyList(type);

        // then
        assertThat(cachedResultList).containsExactlyInAnyOrder("value", "anotherValue");

    }
}

package net.croz.nrich.registry.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.registry.RegistryTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringJUnitWebConfig(RegistryTestConfiguration.class)
public abstract class BaseWebTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        additionSetup();
    }

    void additionSetup() {

    }
}

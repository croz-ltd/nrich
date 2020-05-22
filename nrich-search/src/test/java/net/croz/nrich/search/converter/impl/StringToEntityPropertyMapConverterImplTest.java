package net.croz.nrich.search.converter.impl;

import net.croz.nrich.search.SearchConfigurationTestConfiguration;
import net.croz.nrich.search.converter.stub.StringToEntityPropertyMapConverterImplTestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.Map;

import static net.croz.nrich.search.converter.testutil.ConverterGeneratingUtil.dateOf;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = SearchConfigurationTestConfiguration.class)
public class StringToEntityPropertyMapConverterImplTest {

    @Autowired
    private StringToEntityPropertyMapConverterImpl stringToEntityPropertyMapConverter;

    @Test
    void shouldConvertStringToEntityPropertyMap() {
        // given
        final String value = "01.01.1970";

        // when
        final Map<String, Object> result = stringToEntityPropertyMapConverter.convert(value, Arrays.asList("name", "date", "nestedEntity.nestedName"), StringToEntityPropertyMapConverterImplTestEntity.class);

        // then
        assertThat(result.get("name")).isEqualTo(value);
        assertThat(result.get("date")).isEqualTo(dateOf(value));
        assertThat(result.get("nestedEntity.nestedName")).isEqualTo(value);
    }

    @Test
    void shouldNotFailOnNonExistingAttributes() {
        // given
        final String value = "name";

        // when
        final Map<String, Object> result = stringToEntityPropertyMapConverter.convert(value, Arrays.asList("name", "nonExisting"), StringToEntityPropertyMapConverterImplTestEntity.class);

        // then
        assertThat(result.get("name")).isEqualTo(value);
        assertThat(result.get("nonExisting")).isNull();
    }
}

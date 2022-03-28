package net.croz.nrich.formconfiguration.api.util;

import net.croz.nrich.formconfiguration.api.customizer.FormConfigurationMappingCustomizer;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class FormConfigurationMappingCustomizerUtilTest {

    @Test
    void shouldNotFailOnNullFormConfigurationMappingCustomizerList() {
        // when
        Throwable thrown = catchThrowable(() -> FormConfigurationMappingCustomizerUtil.applyCustomizerList(Collections.emptyMap(), null));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldNotFailOnNullFormConfigurationMapping() {
        // when
        Throwable thrown = catchThrowable(() -> FormConfigurationMappingCustomizerUtil.applyCustomizerList(null, Collections.emptyList()));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldApplyFormConfigurationCustomizer() {
        // given
        String key = "key";
        Class<?> value = Integer.class;
        Map<String, Class<?>> formConfigurationMapping = new HashMap<>();
        FormConfigurationMappingCustomizer customizer = mapping -> mapping.put(key, value);

        // when
        Map<String, Class<?>> result = FormConfigurationMappingCustomizerUtil.applyCustomizerList(formConfigurationMapping, Collections.singletonList(customizer));

        // then
        assertThat(result).containsEntry(key, value);
    }
}

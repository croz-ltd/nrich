package net.croz.nrich.registry.data.util;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClassLoadingUtilTest {

    @Test
    void shouldLoadClass() {
        // given
        List<String> classNameList = Collections.singletonList(String.class.getName());

        // when
        Class<?> result = ClassLoadingUtil.loadClassFromList(classNameList);

        // then
        assertThat(result).isEqualTo(String.class);
    }

    @Test
    void shouldReturnNullForNonExistingClass() {
        // given
        List<String> classNameList = Collections.singletonList("non.existing.Class");

        // when
        Class<?> result = ClassLoadingUtil.loadClassFromList(classNameList);

        // then
        assertThat(result).isNull();
    }
}

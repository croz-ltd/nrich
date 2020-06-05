package net.croz.nrich.registry.core.util;

import lombok.SneakyThrows;
import net.croz.nrich.registry.core.stub.AnnotationUtilTestEntity;
import org.junit.jupiter.api.Test;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationUtilTest {

    @Test
    void shouldReturnFalseWhenAnnotationClassIsNotOnClassPath() {
        // given
        final String nonExistingClass = "non.existing.class.Test";

        // when
        final boolean isAnnotationPresent = AnnotationUtil.isAnnotationPresent(AnnotationUtilTest.class, nonExistingClass);

        // then
        assertThat(isAnnotationPresent).isFalse();
    }

    @Test
    void shouldReturnFalseWhenAnnotationClassIsNotPresent() {
        // given
        final String nonPresentAnnotation = Test.class.getName();

        // when
        final boolean isAnnotationPresent = AnnotationUtil.isAnnotationPresent(AnnotationUtil.class, nonPresentAnnotation);

        // then
        assertThat(isAnnotationPresent).isFalse();
    }

    @Test
    void shouldReturnTrueWhenAnnotationClassIsPresent() {
        // given
        final String presentAnnotationName = Valid.class.getName();

        // when
        final boolean isAnnotationPresent = AnnotationUtil.isAnnotationPresent(AnnotationUtilTestEntity.class, presentAnnotationName);

        // then
        assertThat(isAnnotationPresent).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldReturnFalseWhenAnnotationClassForFieldIsNotOnClassPath() {
        // given
        final Field field = AnnotationUtilTestEntity.class.getField("name");
        final String nonExistingClass = "non.existing.class.Test";

        // when
        final boolean isAnnotationPresent = AnnotationUtil.isAnnotationPresent(field, nonExistingClass);

        // then
        assertThat(isAnnotationPresent).isFalse();
    }

    @SneakyThrows
    @Test
    void shouldReturnFalseWhenAnnotationClassForFieldIsNotPresent() {
        // given
        final Field field = AnnotationUtilTestEntity.class.getField("name");
        final String nonPresentAnnotation = Test.class.getName();

        // when
        final boolean isAnnotationPresent = AnnotationUtil.isAnnotationPresent(field, nonPresentAnnotation);

        // then
        assertThat(isAnnotationPresent).isFalse();
    }

    @SneakyThrows
    @Test
    void shouldReturnTrueWhenAnnotationClassForFieldIsPresent() {
        // given
        final Field field = AnnotationUtilTestEntity.class.getField("name");
        final String presentAnnotationName = NotNull.class.getName();

        // when
        final boolean isAnnotationPresent = AnnotationUtil.isAnnotationPresent(field, presentAnnotationName);

        // then
        assertThat(isAnnotationPresent).isTrue();
    }

}

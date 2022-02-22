package net.croz.nrich.registry.core.util;

import net.croz.nrich.registry.core.stub.AnnotationUtilTestEntity;
import org.junit.jupiter.api.Test;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationUtilTest {

    @Test
    void shouldReturnFalseWhenAnnotationClassIsNotOnClassPath() {
        // given
        String nonExistingClass = "non.existing.class.Test";

        // when
        boolean isAnnotationPresent = AnnotationUtil.isAnnotationPresent(AnnotationUtilTest.class, nonExistingClass);

        // then
        assertThat(isAnnotationPresent).isFalse();
    }

    @Test
    void shouldReturnFalseWhenAnnotationClassIsNotPresent() {
        // given
        String nonPresentAnnotation = Test.class.getName();

        // when
        boolean isAnnotationPresent = AnnotationUtil.isAnnotationPresent(AnnotationUtil.class, nonPresentAnnotation);

        // then
        assertThat(isAnnotationPresent).isFalse();
    }

    @Test
    void shouldReturnTrueWhenAnnotationClassIsPresent() {
        // given
        String presentAnnotationName = Valid.class.getName();

        // when
        boolean isAnnotationPresent = AnnotationUtil.isAnnotationPresent(AnnotationUtilTestEntity.class, presentAnnotationName);

        // then
        assertThat(isAnnotationPresent).isTrue();
    }

    @Test
    void shouldReturnFalseWhenAnnotationClassForFieldIsNotOnClassPath() throws Exception {
        // given
        Field field = AnnotationUtilTestEntity.class.getField("name");
        String nonExistingClass = "non.existing.class.Test";

        // when
        boolean isAnnotationPresent = AnnotationUtil.isAnnotationPresent(field, nonExistingClass);

        // then
        assertThat(isAnnotationPresent).isFalse();
    }

    @Test
    void shouldReturnFalseWhenAnnotationClassForFieldIsNotPresent() throws Exception {
        // given
        Field field = AnnotationUtilTestEntity.class.getField("name");
        String nonPresentAnnotation = Test.class.getName();

        // when
        boolean isAnnotationPresent = AnnotationUtil.isAnnotationPresent(field, nonPresentAnnotation);

        // then
        assertThat(isAnnotationPresent).isFalse();
    }

    @Test
    void shouldReturnTrueWhenAnnotationClassForFieldIsPresent() throws Exception {
        // given
        Field field = AnnotationUtilTestEntity.class.getField("name");
        String presentAnnotationName = NotNull.class.getName();

        // when
        boolean isAnnotationPresent = AnnotationUtil.isAnnotationPresent(field, presentAnnotationName);

        // then
        assertThat(isAnnotationPresent).isTrue();
    }

}

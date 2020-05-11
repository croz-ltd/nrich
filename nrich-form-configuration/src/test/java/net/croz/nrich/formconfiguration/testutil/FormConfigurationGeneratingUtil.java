package net.croz.nrich.formconfiguration.testutil;

import net.croz.nrich.formconfiguration.model.ConstrainedProperty;

import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.Collections;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class FormConfigurationGeneratingUtil {

    private FormConfigurationGeneratingUtil() {
    }

    public static ConstrainedProperty createConstrainedProperty(final Class<?> parentType) {
        @SuppressWarnings("unchecked")
        final ConstraintDescriptor<Annotation> constraintDescriptor = mock(ConstraintDescriptor.class);
        final Annotation annotation = mock(Annotation.class);

        doReturn(NotNull.class).when(annotation).annotationType();

        when(constraintDescriptor.getAttributes()).thenReturn(Collections.emptyMap());
        when(constraintDescriptor.getAnnotation()).thenReturn(annotation);

        return ConstrainedProperty.builder()
                .constraintDescriptor(constraintDescriptor)
                .name("propertyName")
                .path("propertyPath")
                .type(String.class)
                .parentType(parentType).build();

    }
}

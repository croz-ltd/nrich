package net.croz.nrich.search.converter;

import net.croz.nrich.search.SearchTestConfiguration;
import net.croz.nrich.search.converter.stub.DefaultStringToEntityPropertyMapConverterTestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.ManagedType;
import java.util.Arrays;
import java.util.Map;

import static net.croz.nrich.search.converter.testutil.ConverterGeneratingUtil.dateOf;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(SearchTestConfiguration.class)
class DefaultStringToEntityPropertyMapConverterTest {

    @Autowired
    private DefaultStringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldConvertStringToEntityPropertyMap() {
        // given
        String value = "01.01.1970.";

        // when
        Map<String, Object> result = stringToEntityPropertyMapConverter.convert(value, Arrays.asList("name", "date", "nestedEntity.nestedName"), managedTypeOfTestEntity());

        // then
        assertThat(result).containsEntry("name", value).containsEntry("date", dateOf(value)).containsEntry("nestedEntity.nestedName", value);
    }

    @Test
    void shouldNotFailOnNonExistingAttributes() {
        // given
        String value = "name";

        // when
        Map<String, Object> result = stringToEntityPropertyMapConverter.convert(value, Arrays.asList("name", "nonExisting"), managedTypeOfTestEntity());

        // then
        assertThat(result).containsEntry("name", value);
        assertThat(result.get("nonExisting")).isNull();
    }

    @Test
    void shouldReturnEmptyMapWhenValueIsNull() {
        // given
        String value = null;

        // when
        Map<String, Object> result = stringToEntityPropertyMapConverter.convert(value, Arrays.asList("name", "nonExisting"), managedTypeOfTestEntity());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyMapWhenPropertyListIsEmpty() {
        // given
        String value = "value";

        // when
        Map<String, Object> result = stringToEntityPropertyMapConverter.convert(value, null, managedTypeOfTestEntity());

        // then
        assertThat(result).isEmpty();
    }

    private ManagedType<?> managedTypeOfTestEntity() {
        return entityManager.getMetamodel().managedType((Class<?>) DefaultStringToEntityPropertyMapConverterTestEntity.class);
    }
}

/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.search.converter;

import net.croz.nrich.search.SearchTestConfiguration;
import net.croz.nrich.search.api.model.property.SearchPropertyConfiguration;
import net.croz.nrich.search.converter.stub.DefaultStringToEntityPropertyMapConverterTestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.ManagedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.croz.nrich.search.converter.testutil.ConverterGeneratingUtil.dateOf;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(SearchTestConfiguration.class)
class DefaultStringToEntityPropertyMapConverterTest {

    private static final SearchPropertyConfiguration PROPERTY_CONFIGURATION = SearchPropertyConfiguration.defaultSearchPropertyConfiguration();

    @Autowired
    private DefaultStringToEntityPropertyMapConverter stringToEntityPropertyMapConverter;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldConvertStringToEntityPropertyMap() {
        // given
        String value = "01.01.1970.";

        // when
        Map<String, Object> result = stringToEntityPropertyMapConverter.convert(value, Arrays.asList("name", "date", "nestedEntity.nestedName"), managedTypeOfTestEntity(), PROPERTY_CONFIGURATION);

        // then
        assertThat(result).containsEntry("name", value).containsEntry("date", dateOf(value)).containsEntry("nestedEntity.nestedName", value);
    }

    @Test
    void shouldNotFailOnNonExistingAttributes() {
        // given
        String value = "name";

        // when
        Map<String, Object> result = stringToEntityPropertyMapConverter.convert(value, Arrays.asList("name", "nonExisting"), managedTypeOfTestEntity(), PROPERTY_CONFIGURATION);

        // then
        assertThat(result).containsEntry("name", value);
        assertThat(result.get("nonExisting")).isNull();
    }

    @Test
    void shouldReturnEmptyMapWhenValueIsNull() {
        // given
        String value = null;

        // when
        Map<String, Object> result = stringToEntityPropertyMapConverter.convert(value, Arrays.asList("name", "nonExisting"), managedTypeOfTestEntity(), PROPERTY_CONFIGURATION);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyMapWhenPropertyListIsEmpty() {
        // given
        String value = "value";

        // when
        Map<String, Object> result = stringToEntityPropertyMapConverter.convert(value, null, managedTypeOfTestEntity(), PROPERTY_CONFIGURATION);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldConvertPropertyWithSuffix() {
        // given
        String value = "01.01.1970.";
        List<String> propertyToSearchList = Arrays.asList("dateFrom", "dateFromIncluding", "dateTo", "dateToIncluding");

        // when
        Map<String, Object> result = stringToEntityPropertyMapConverter.convert(value, propertyToSearchList, managedTypeOfTestEntity(), PROPERTY_CONFIGURATION);

        // then
        assertThat(result).containsKeys(propertyToSearchList.toArray(new String[0])).containsValue(dateOf(value));
    }

    @Test
    void shouldSupportSearchingAssociationsById() {
        // given
        String value = "1";
        List<String> propertyToSearchList = List.of("nestedEntity", "nestedEntityList");

        // when
        Map<String, Object> result = stringToEntityPropertyMapConverter.convert(value, propertyToSearchList, managedTypeOfTestEntity(), PROPERTY_CONFIGURATION);

        // then
        assertThat(result).containsKeys("nestedEntity.id", "nestedEntityList.id").containsValue(Long.parseLong(value));
    }

    private ManagedType<?> managedTypeOfTestEntity() {
        return entityManager.getMetamodel().managedType((Class<?>) DefaultStringToEntityPropertyMapConverterTestEntity.class);
    }
}

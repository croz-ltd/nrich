package net.croz.nrich.registry.configuration.service;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.configuration.model.JavascriptType;
import net.croz.nrich.registry.configuration.model.RegistryEntityConfiguration;
import net.croz.nrich.registry.configuration.model.RegistryCategoryConfiguration;
import net.croz.nrich.registry.configuration.model.RegistryPropertyConfiguration;
import net.croz.nrich.registry.configuration.stub.RegistryConfigurationTestEntity;
import net.croz.nrich.registry.configuration.stub.RegistryConfigurationTestEntityWithAssociationAndEmbeddedId;
import net.croz.nrich.registry.configuration.stub.RegistryConfigurationTestEntityWithIdClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitWebConfig(RegistryTestConfiguration.class)
public class DefaultRegistryConfigurationServiceTest {

    @Autowired
    private DefaultRegistryConfigurationService registryConfigurationService;

    @Test
    void shouldResolveRegistryConfiguration() {
        // when
        final List<RegistryCategoryConfiguration> result = registryConfigurationService.fetchRegistryCategoryConfigurationList();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(3);
        assertThat(result).extracting("registryCategoryId").containsExactly("CONFIGURATION", "DATA", "HISTORY");
        assertThat(result).extracting("registryCategoryIdDisplay").containsExactly("Configuration group", "Data group", "HISTORY");
    }

    @Test
    void shouldResolveConfigurationWithOverrideDefined() {
        // when
        final List<RegistryCategoryConfiguration> result = registryConfigurationService.fetchRegistryCategoryConfigurationList();
        final RegistryCategoryConfiguration registryTestEntityConfiguration = result.get(0);
        final RegistryEntityConfiguration registryEntityConfiguration = registryTestEntityConfiguration.getRegistryEntityConfigurationList().stream()
                .filter(entityConfig -> RegistryConfigurationTestEntity.class.getName().equals(entityConfig.getRegistryId()))
                .findFirst()
                .orElse(null);

        // then
        assertThat(registryEntityConfiguration).isNotNull();

        assertThat(registryEntityConfiguration.getCategory()).isEqualTo("CONFIGURATION");

        assertThat(registryEntityConfiguration.getRegistryName()).isEqualTo(RegistryConfigurationTestEntity.class.getSimpleName());
        assertThat(registryEntityConfiguration.getRegistryDisplayName()).isEqualTo("Test entity");
        assertThat(registryEntityConfiguration.isReadOnly()).isFalse();
        assertThat(registryEntityConfiguration.isCreatable()).isTrue();
        assertThat(registryEntityConfiguration.isUpdateable()).isTrue();
        assertThat(registryEntityConfiguration.isDeletable()).isFalse();
        assertThat(registryEntityConfiguration.isIdentifierAssigned()).isTrue();
        assertThat(registryEntityConfiguration.isCompositeIdentity()).isFalse();
        assertThat(registryEntityConfiguration.getCompositeIdentityPropertyNameList()).isNullOrEmpty();
        assertThat(registryEntityConfiguration.isHistoryAvailable()).isFalse();

        assertThat(registryEntityConfiguration.getRegistryPropertyConfigurationList()).hasSize(5);
        assertThat(registryEntityConfiguration.getRegistryPropertyConfigurationList()).extracting("name").containsExactly("name", "id", "nonEditableProperty", "floatNumber", "doubleNumber");
        assertThat(registryEntityConfiguration.getRegistryPropertyConfigurationList()).extracting("isDecimal").containsExactly(false, false, false, true, true);

        // and when
        final RegistryPropertyConfiguration nameConfiguration = registryEntityConfiguration.getRegistryPropertyConfigurationList().get(0);

        // then
        assertThat(nameConfiguration.getJavascriptType()).isEqualTo(JavascriptType.STRING);
        assertThat(nameConfiguration.getOriginalType()).isEqualTo(String.class.getName());
        assertThat(nameConfiguration.isId()).isFalse();
        assertThat(nameConfiguration.isDecimal()).isFalse();
        assertThat(nameConfiguration.isSingularAssociation()).isFalse();
        assertThat(nameConfiguration.getFormLabel()).isEqualTo("Name of property");
        assertThat(nameConfiguration.getColumnHeader()).isEqualTo("Header of property");
        assertThat(nameConfiguration.isEditable()).isTrue();
        assertThat(nameConfiguration.isSortable()).isTrue();

        // and when
        final RegistryPropertyConfiguration idPropertyConfiguration = registryEntityConfiguration.getRegistryPropertyConfigurationList().get(1);

        // then
        assertThat(idPropertyConfiguration.isId()).isTrue();

        // and when
        final RegistryPropertyConfiguration nonEditablePropertyConfiguration = registryEntityConfiguration.getRegistryPropertyConfigurationList().get(2);

        // then
        assertThat(nonEditablePropertyConfiguration.getJavascriptType()).isEqualTo(JavascriptType.STRING);
        assertThat(nonEditablePropertyConfiguration.getOriginalType()).isEqualTo(String.class.getName());
        assertThat(nonEditablePropertyConfiguration.isId()).isFalse();
        assertThat(nameConfiguration.isDecimal()).isFalse();
        assertThat(nameConfiguration.isSingularAssociation()).isFalse();
        assertThat(nonEditablePropertyConfiguration.isEditable()).isFalse();
        assertThat(nonEditablePropertyConfiguration.isSortable()).isFalse();

        // and when
        final List<RegistryPropertyConfiguration> registryHistoryPropertyConfigurationList = registryTestEntityConfiguration.getRegistryHistoryPropertyConfigurationList();

        // then
        assertThat(registryHistoryPropertyConfigurationList).isNotEmpty();
        assertThat(registryHistoryPropertyConfigurationList).extracting("name").containsExactlyInAnyOrder("id", "timestamp", "revisionProperty");
        assertThat(registryHistoryPropertyConfigurationList).extracting("formLabel").containsExactlyInAnyOrder("Revision number", "Revision timestamp", "Revision property");
        assertThat(registryHistoryPropertyConfigurationList).extracting("columnHeader").containsExactlyInAnyOrder("Revision number", "Revision timestamp", "Revision property");
    }

    @Test
    void shouldResolveRegistryConfigurationForComplexEntitiesWithAssociationsAndEmbeddedId() {
        // when
        final List<RegistryCategoryConfiguration> result = registryConfigurationService.fetchRegistryCategoryConfigurationList();
        final RegistryCategoryConfiguration registryTestEntityConfiguration = result.get(0);
        final RegistryEntityConfiguration registryEntityConfiguration = registryTestEntityConfiguration.getRegistryEntityConfigurationList().stream()
                .filter(entityConfig -> RegistryConfigurationTestEntityWithAssociationAndEmbeddedId.class.getName().equals(entityConfig.getRegistryId()))
                .findFirst()
                .orElse(null);


        // then
        assertThat(registryEntityConfiguration).isNotNull();

        assertThat(registryEntityConfiguration.isIdentifierAssigned()).isTrue();
        assertThat(registryEntityConfiguration.isCompositeIdentity()).isTrue();
        assertThat(registryEntityConfiguration.getCompositeIdentityPropertyNameList()).containsExactlyInAnyOrder("id.firstId", "id.secondId");

        assertThat(registryEntityConfiguration.getRegistryPropertyConfigurationList()).extracting("name").containsExactly("id", "amount", "registryConfigurationTestEntityManyToOne", "registryConfigurationTestEntityOneToOne");

        // and when
        final RegistryPropertyConfiguration numberRegistryConfiguration = registryEntityConfiguration.getRegistryPropertyConfigurationList().get(1);

        // then
        assertThat(numberRegistryConfiguration.isDecimal()).isTrue();
        assertThat(numberRegistryConfiguration.getJavascriptType()).isEqualTo(JavascriptType.NUMBER);

        // and when
        final RegistryPropertyConfiguration manyToOnePropertyConfiguration = registryEntityConfiguration.getRegistryPropertyConfigurationList().get(2);

        // then
        assertThat(manyToOnePropertyConfiguration.isSingularAssociation()).isTrue();
        assertThat(manyToOnePropertyConfiguration.getSingularAssociationReferencedClass()).isEqualTo(RegistryConfigurationTestEntity.class.getName());

        // and when
        final RegistryPropertyConfiguration oneToOnePropertyConfiguration = registryEntityConfiguration.getRegistryPropertyConfigurationList().get(3);

        // then
        assertThat(oneToOnePropertyConfiguration.isSingularAssociation()).isTrue();
        assertThat(oneToOnePropertyConfiguration.getSingularAssociationReferencedClass()).isEqualTo(RegistryConfigurationTestEntity.class.getName());
    }

    @Test
    void shouldResolveRegistryConfigurationForComplexEntitiesWithIdClass() {
        // when
        final List<RegistryCategoryConfiguration> result = registryConfigurationService.fetchRegistryCategoryConfigurationList();
        final RegistryCategoryConfiguration registryTestEntityConfiguration = result.get(0);
        final RegistryEntityConfiguration registryEntityConfiguration = registryTestEntityConfiguration.getRegistryEntityConfigurationList().stream()
                .filter(entityConfig -> RegistryConfigurationTestEntityWithIdClass.class.getName().equals(entityConfig.getRegistryId()))
                .findFirst()
                .orElse(null);


        // then
        assertThat(registryEntityConfiguration).isNotNull();

        assertThat(registryEntityConfiguration.isIdentifierAssigned()).isTrue();
        assertThat(registryEntityConfiguration.isCompositeIdentity()).isTrue();
        assertThat(registryEntityConfiguration.getCompositeIdentityPropertyNameList()).containsExactlyInAnyOrder("firstId", "secondId");

        assertThat(registryEntityConfiguration.getRegistryPropertyConfigurationList()).extracting("name").containsExactlyInAnyOrder("firstId", "secondId", "name");
        assertThat(registryEntityConfiguration.getRegistryPropertyConfigurationList()).extracting("isId").containsExactlyInAnyOrder(true, true, false);
    }
}

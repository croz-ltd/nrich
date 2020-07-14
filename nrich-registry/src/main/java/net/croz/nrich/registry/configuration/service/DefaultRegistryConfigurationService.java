package net.croz.nrich.registry.configuration.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.api.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.configuration.comparator.RegistryGroupConfigurationComparator;
import net.croz.nrich.registry.configuration.comparator.RegistryPropertyComparator;
import net.croz.nrich.registry.configuration.constants.RegistryConfigurationConstants;
import net.croz.nrich.registry.configuration.model.JavascriptType;
import net.croz.nrich.registry.configuration.model.RegistryCategoryConfiguration;
import net.croz.nrich.registry.configuration.model.RegistryEntityConfiguration;
import net.croz.nrich.registry.configuration.model.RegistryPropertyConfiguration;
import net.croz.nrich.registry.configuration.util.JavaToJavascriptTypeConversionUtil;
import net.croz.nrich.registry.core.constants.RegistryEnversConstants;
import net.croz.nrich.registry.core.model.PropertyWithType;
import net.croz.nrich.registry.core.model.RegistryCategoryDefinitionHolder;
import net.croz.nrich.registry.core.model.RegistryHistoryConfigurationHolder;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
import net.croz.nrich.registry.core.util.AnnotationUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultRegistryConfigurationService implements RegistryConfigurationService {

    private final MessageSource messageSource;

    private final List<String> readOnlyPropertyList;

    private final RegistryCategoryDefinitionHolder registryCategoryDefinitionHolder;

    private final RegistryHistoryConfigurationHolder registryHistoryConfiguration;

    private final Map<Class<?>, RegistryOverrideConfiguration> registryOverrideConfigurationMap;

    @Cacheable("nrich.registryConfiguration.cache")
    @Override
    public List<RegistryCategoryConfiguration> fetchRegistryCategoryConfigurationList() {
        final List<RegistryCategoryConfiguration> registryCategoryConfigurationList = new ArrayList<>();

        final List<RegistryPropertyConfiguration> registryPropertyHistoryConfigurationList = resolveHistoryPropertyList(registryHistoryConfiguration);

        registryCategoryDefinitionHolder.getRegistryCategoryDefinitionList().forEach(registryGroupDefinition -> {
            final String registryGroupIdDisplay = groupDisplayLabel(registryGroupDefinition.getRegistryGroupId());

            final List<RegistryEntityConfiguration> registryEntityConfigurationList = registryGroupDefinition.getRegistryEntityList().stream()
                    .map(managedType -> resolveRegistryConfiguration(registryGroupDefinition.getRegistryGroupId(), managedType, registryPropertyHistoryConfigurationList))
                    .sorted(Comparator.comparing(RegistryEntityConfiguration::getRegistryId))
                    .collect(Collectors.toList());

            final RegistryCategoryConfiguration registryConfiguration = new RegistryCategoryConfiguration(registryGroupDefinition.getRegistryGroupId(), registryGroupIdDisplay, registryEntityConfigurationList);

            registryCategoryConfigurationList.add(registryConfiguration);
        });

        registryCategoryConfigurationList.sort(new RegistryGroupConfigurationComparator(registryCategoryDefinitionHolder.getRegistryCategoryDisplayOrderList()));

        return registryCategoryConfigurationList;
    }

    private RegistryEntityConfiguration resolveRegistryConfiguration(final String registryGroupId, final ManagedType<?> managedType, final List<RegistryPropertyConfiguration> registryPropertyHistoryConfigurationList) {
        final Class<?> entityType = managedType.getJavaType();
        final RegistryOverrideConfiguration registryOverrideConfiguration = resolveRegistryOverrideConfiguration(entityType, registryOverrideConfigurationMap);
        final ManagedTypeWrapper managedTypeWrapper = new ManagedTypeWrapper(managedType);

        final String registryDisplayName = registryDisplayLabel(entityType);
        final boolean isHistoryAvailable = registryOverrideConfiguration.isHistoryAvailable() || isAudited(entityType);
        final List<RegistryPropertyConfiguration> registryPropertyConfigurationList = resolveRegistryPropertyListForType(managedTypeWrapper, registryOverrideConfiguration);
        final List<RegistryPropertyConfiguration> registryEmbeddedIdPropertyConfigurationList = resolverEmbeddedIdPropertyConfigurationList(managedTypeWrapper, registryOverrideConfiguration);
        final List<String> registryPropertyDisplayOrderList = Optional.ofNullable(registryOverrideConfiguration.getPropertyDisplayOrderList()).orElse(Collections.emptyList());

        registryPropertyConfigurationList.sort(new RegistryPropertyComparator(registryPropertyDisplayOrderList));

        return RegistryEntityConfiguration.builder()
                .category(registryGroupId)
                .registryId(entityType.getName())
                .registryName(entityType.getSimpleName())
                .registryDisplayName(registryDisplayName)
                .registryPropertyConfigurationList(registryPropertyConfigurationList)
                .registryEmbeddedIdPropertyConfigurationList(registryEmbeddedIdPropertyConfigurationList)
                .registryHistoryPropertyConfigurationList(registryPropertyHistoryConfigurationList)
                .readOnly(registryOverrideConfiguration.isReadOnly())
                .creatable(registryOverrideConfiguration.isCreatable())
                .updateable(registryOverrideConfiguration.isUpdateable())
                .deletable(registryOverrideConfiguration.isDeletable())
                .isHistoryAvailable(isHistoryAvailable)
                .isIdentifierAssigned(managedTypeWrapper.isIdentifierAssigned())
                .isIdClassIdentity(managedTypeWrapper.isIdClassIdentifier())
                .isEmbeddedIdentity(managedTypeWrapper.isEmbeddedIdentifier())
                .idClassPropertyNameList(managedTypeWrapper.getIdClassPropertyNameList())
                .build();
    }

    private List<RegistryPropertyConfiguration> resolveRegistryPropertyListForType(final ManagedTypeWrapper managedTypeWrapper, final RegistryOverrideConfiguration registryOverrideConfiguration) {
        final Predicate<String> isIdAttributePredicate = (attributeName) -> attributeName.equals(managedTypeWrapper.getIdAttributeName()) || managedTypeWrapper.getIdClassPropertyNameList().contains(attributeName);

        return resolveManagedTypePropertyList(managedTypeWrapper.getIdentifiableType(), managedTypeWrapper.getIdentifiableType().getJavaType(), null, isIdAttributePredicate, !managedTypeWrapper.isIdentifierAssigned(), registryOverrideConfiguration);
    }

    private List<RegistryPropertyConfiguration> resolverEmbeddedIdPropertyConfigurationList(final ManagedTypeWrapper managedTypeWrapper, final RegistryOverrideConfiguration registryOverrideConfiguration) {
        if (!managedTypeWrapper.isEmbeddedIdentifier()) {
            return Collections.emptyList();
        }

        return resolveManagedTypePropertyList(managedTypeWrapper.getEmbeddableIdType(), managedTypeWrapper.getIdentifiableType().getJavaType(), managedTypeWrapper.getIdAttributeName(), (attribute) -> false, true, registryOverrideConfiguration);
    }

    private List<RegistryPropertyConfiguration> resolveManagedTypePropertyList(final ManagedType<?> managedType, final Class<?> entityType, final String prefix, final Predicate<String> isIdAttributePredicate, final boolean isIdReadOnly, final RegistryOverrideConfiguration registryOverrideConfiguration) {
        final List<String> ignoredPropertyList = Optional.ofNullable(registryOverrideConfiguration.getIgnoredPropertyList()).orElse(Collections.emptyList());
        final List<String> readOnlyOverridePropertyList = Optional.ofNullable(registryOverrideConfiguration.getNonEditablePropertyList()).orElse(Collections.emptyList());
        final List<String> nonSortablePropertyList = Optional.ofNullable(registryOverrideConfiguration.getNonSortablePropertyList()).orElse(Collections.emptyList());
        final List<String> nonSearchablePropertyList = Optional.ofNullable(registryOverrideConfiguration.getNonSearchablePropertyList()).orElse(Collections.emptyList());

        final List<RegistryPropertyConfiguration> registryPropertyConfigurationList = new ArrayList<>();

        managedType.getAttributes().forEach(attribute -> {
            if (shouldSkipAttribute(ignoredPropertyList, attribute)) {
                return;
            }

            final String attributeName = prefix == null ? attribute.getName() : String.format(RegistryConfigurationConstants.REGISTRY_PROPERTY_PREFIX_FORMAT, prefix, attribute.getName());
            final Class<?> attributeType = attribute.getJavaType();

            final boolean isIdAttribute = isIdAttributePredicate.test(attributeName);
            final boolean isSingularAssociation = attribute.isAssociation() && attribute instanceof SingularAttribute;
            final Class<?> singularAssociationReferencedClass = isSingularAssociation ? resolveSingularAssociationReferencedClass(attribute) : null;

            final boolean isReadOnly = isIdAttribute ? isIdReadOnly : readOnlyPropertyList.contains(attributeName) || readOnlyOverridePropertyList.contains(attributeName);
            final boolean isSortable = !nonSortablePropertyList.contains(attributeName);
            final boolean isSearchable = !nonSearchablePropertyList.contains(attributeName);

            final RegistryPropertyConfiguration registryPropertyConfiguration = resolveRegistryPropertyConfiguration(entityType.getName(), attributeType, attributeName, isIdAttribute, isSingularAssociation, singularAssociationReferencedClass, isReadOnly, isSortable, isSearchable);

            registryPropertyConfigurationList.add(registryPropertyConfiguration);

        });

        return registryPropertyConfigurationList;
    }

    private List<RegistryPropertyConfiguration> resolveHistoryPropertyList(final RegistryHistoryConfigurationHolder registryHistoryConfiguration) {
        final List<PropertyWithType> historyPropertyList = new ArrayList<>();

        historyPropertyList.add(registryHistoryConfiguration.getRevisionNumberProperty());
        historyPropertyList.add(registryHistoryConfiguration.getRevisionTimestampProperty());
        historyPropertyList.add(registryHistoryConfiguration.getRevisionTypeProperty());
        historyPropertyList.addAll(registryHistoryConfiguration.getRevisionAdditionalPropertyList());

        return historyPropertyList.stream()
                .map(propertyWithType -> resolveRegistryPropertyConfiguration(RegistryConfigurationConstants.REGISTRY_REVISION_ENTITY_PREFIX, propertyWithType.getType(), propertyWithType.getName(), false, false, null, true, true, false))
                .sorted(new RegistryPropertyComparator(registryHistoryConfiguration.getPropertyDisplayList()))
                .collect(Collectors.toList());
    }

    private RegistryPropertyConfiguration resolveRegistryPropertyConfiguration(final String entityTypePrefix, final Class<?> attributeType, final String attributeName, final boolean isIdAttribute, final boolean isSingularAssociation, final Class<?> singularAssociationReferencedClass, final boolean isReadOnly, final boolean isSortable, final boolean isSearchable) {
        final JavascriptType javascriptType = JavaToJavascriptTypeConversionUtil.fromJavaType(attributeType);
        final boolean isDecimal = JavaToJavascriptTypeConversionUtil.isDecimal(attributeType);

        final String formLabel = formLabel(entityTypePrefix, attributeType, attributeName);
        final String columnHeader = columnHeader(entityTypePrefix, attributeType, attributeName);

        return RegistryPropertyConfiguration.builder()
                .name(attributeName)
                .originalType(attributeType.getName())
                .javascriptType(javascriptType)
                .isDecimal(isDecimal)
                .isSingularAssociation(isSingularAssociation)
                .singularAssociationReferencedClass(Optional.ofNullable(singularAssociationReferencedClass).map(Class::getName).orElse(null))
                .isId(isIdAttribute)
                .formLabel(formLabel)
                .columnHeader(columnHeader)
                .editable(!isReadOnly)
                .searchable(isSearchable)
                .sortable(isSortable)
                .build();
    }

    private Class<?> resolveSingularAssociationReferencedClass(final Attribute<?, ?> attribute) {
        return ((ManagedType<?>) ((SingularAttribute<?, ?>) attribute).getType()).getJavaType();
    }

    private String groupDisplayLabel(final String groupId) {
        final String[] groupMessageCodeList = { String.format(RegistryConfigurationConstants.REGISTRY_GROUP_DISPLAY_LABEL_FORMAT, groupId) };
        final DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(groupMessageCodeList, groupId);

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    private String registryDisplayLabel(final Class<?> entityType) {
        final String[] groupMessageCodeList = { String.format(RegistryConfigurationConstants.REGISTRY_NAME_DISPLAY_LABEL_FORMAT, entityType.getName()) };
        final DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(groupMessageCodeList, entityType.getSimpleName());

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    private String formLabel(final String entityTypePrefix, final Class<?> attributeType, final String attributeName) {
        final DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(labelMessageCodeList(entityTypePrefix, attributeType, attributeName).toArray(new String[0]), attributeName);

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    private String columnHeader(final String entityTypePrefix, final Class<?> attributeType, final String attributeName) {
        final List<String> headerMessageCodeList = new ArrayList<>();

        headerMessageCodeList.add(String.format(RegistryConfigurationConstants.REGISTRY_COLUMN_HEADER_FORMAT, entityTypePrefix, attributeName));
        headerMessageCodeList.addAll(labelMessageCodeList(entityTypePrefix, attributeType, attributeName));

        final DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(headerMessageCodeList.toArray(new String[0]), attributeName);

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    private List<String> labelMessageCodeList(final String entityTypePrefix, final Class<?> attributeType, final String attributeName) {
        return Arrays.asList(
                String.format(RegistryConfigurationConstants.REGISTRY_FIELD_DISPLAY_LABEL_FORMAT, entityTypePrefix, attributeName),
                String.format(RegistryConfigurationConstants.REGISTRY_FIELD_DISPLAY_LABEL_FORMAT, attributeName, attributeType.getName()),
                String.format(RegistryConfigurationConstants.REGISTRY_FIELD_DISPLAY_LABEL_SHORT_FORMAT, attributeType.getName())
        );
    }

    private boolean shouldSkipAttribute(final List<String> entityIgnoredPropertyList, final Attribute<?, ?> attribute) {
        return attribute.isCollection() || entityIgnoredPropertyList.contains(attribute.getName());
    }

    private boolean isAudited(final Class<?> entityType) {
        return AnnotationUtil.isAnnotationPresent(entityType, RegistryEnversConstants.ENVERS_AUDITED_ANNOTATION);
    }

    private RegistryOverrideConfiguration resolveRegistryOverrideConfiguration(final Class<?> type, final Map<Class<?>, RegistryOverrideConfiguration> registryOverrideConfigurationMap) {
        if (registryOverrideConfigurationMap == null || registryOverrideConfigurationMap.get(type) == null) {
            return RegistryOverrideConfiguration.defaultConfiguration();
        }

        return registryOverrideConfigurationMap.get(type);
    }
}

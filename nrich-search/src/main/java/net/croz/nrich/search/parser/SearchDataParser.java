package net.croz.nrich.search.parser;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.api.model.operator.DefaultSearchOperator;
import net.croz.nrich.search.api.model.operator.SearchOperator;
import net.croz.nrich.search.api.model.operator.SearchOperatorOverride;
import net.croz.nrich.search.api.model.property.SearchPropertyConfiguration;
import net.croz.nrich.search.api.model.property.SearchPropertyMapping;
import net.croz.nrich.search.bean.MapSupportingDirectFieldAccessFallbackBeanWrapper;
import net.croz.nrich.search.model.AttributeHolder;
import net.croz.nrich.search.model.Restriction;
import net.croz.nrich.search.model.SearchDataParserConfiguration;
import net.croz.nrich.search.support.JpaEntityAttributeResolver;
import org.springframework.util.StringUtils;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SearchDataParser {

    private final ManagedType<?> managedType;

    private final Object searchData;

    private final SearchDataParserConfiguration searchConfiguration;

    public Set<Restriction> resolveRestrictionList() {
        return resolveRestrictionList(null);
    }

    public Set<Restriction> resolveRestrictionList(final String propertyPrefix) {
        return resolveRestrictionListInternal(new MapSupportingDirectFieldAccessFallbackBeanWrapper(searchData), propertyPrefix, null, managedType, new HashSet<>(), false);
    }

    private Set<Restriction> resolveRestrictionListInternal(final MapSupportingDirectFieldAccessFallbackBeanWrapper wrapper, final String propertyPrefix, final String path, final ManagedType<?> managedType, final Set<Restriction> restrictionList, final boolean isPluralAttribute) {
        final List<String> fieldNameList = resolveFieldNameList(wrapper);
        final JpaEntityAttributeResolver attributeResolver = new JpaEntityAttributeResolver(managedType);

        fieldNameList.forEach(originalFieldName -> {
            final String fieldNameWithoutPrefixAndSuffix = fieldNameWithoutSuffixAndPrefix(originalFieldName, propertyPrefix);
            final Object value = wrapper.getPropertyValue(originalFieldName);

            if (value == null) {
                return;
            }

            AttributeHolder attributeHolder = attributeResolver.resolveAttributeByPath(fieldNameWithoutPrefixAndSuffix);

            if (attributeHolder.getAttribute() != null) {
                final String currentPath = path == null ? fieldNameWithoutPrefixAndSuffix : path + "." + fieldNameWithoutPrefixAndSuffix;

                if (attributeHolder.getManagedType() != null) {
                    resolveRestrictionListInternal(new MapSupportingDirectFieldAccessFallbackBeanWrapper(value), propertyPrefix, currentPath, attributeHolder.getManagedType(), restrictionList, attributeHolder.isPlural());
                    return;
                }

                restrictionList.add(createAttributeRestriction(attributeHolder.getAttribute().getJavaType(), originalFieldName, currentPath, value, isPluralAttribute));
            }
            else if (searchUsingPropertyMapping(searchConfiguration)) {
                String mappedPath = findPathUsingMapping(searchConfiguration.getPropertyMappingList(), originalFieldName);

                if (mappedPath == null) {
                    mappedPath = findPathUsingAttributePrefix(originalFieldName, managedType);
                }

                if (mappedPath != null) {
                    attributeHolder = attributeResolver.resolveAttributeByPath(mappedPath);

                    if (attributeHolder.getAttribute() != null) {
                        restrictionList.add(createAttributeRestriction(attributeHolder.getAttribute().getJavaType(), originalFieldName, mappedPath, value, attributeHolder.isPlural()));
                    }
                }
            }
        });

        return restrictionList;
    }

    private List<String> resolveFieldNameList(final MapSupportingDirectFieldAccessFallbackBeanWrapper wrapper) {
        final List<String> ignoredFieldList = searchConfiguration.getSearchPropertyConfiguration().getSearchIgnoredFieldList() == null ? Collections.emptyList() : searchConfiguration.getSearchPropertyConfiguration().getSearchIgnoredFieldList();

        if (wrapper.getEntityAsMap() != null) {
            return wrapper.getEntityAsMap().keySet().stream()
                    .filter(key -> !ignoredFieldList.contains(key))
                    .collect(Collectors.toList());
        }

        final Predicate<Field> shouldIncludeField = field -> !(ignoredFieldList.contains(field.getName()) || field.isSynthetic() || Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()));

        return Arrays.stream(wrapper.getRootClass().getDeclaredFields())
                .filter(shouldIncludeField)
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    private String fieldNameWithoutSuffixAndPrefix(final String originalFieldName, final String prefix) {
        final SearchPropertyConfiguration searchPropertyConfiguration = searchConfiguration.getSearchPropertyConfiguration();
        final String[] suffixListToRemove = new String[] { searchPropertyConfiguration.getRangeQueryFromIncludingSuffix(), searchPropertyConfiguration.getRangeQueryFromSuffix(), searchPropertyConfiguration.getRangeQueryToIncludingSuffix(), searchPropertyConfiguration.getRangeQueryToSuffix(), searchPropertyConfiguration.getCollectionQuerySuffix() };

        String fieldName = originalFieldName;
        for (final String suffix : suffixListToRemove) {
            if (originalFieldName.endsWith(suffix)) {
                fieldName = originalFieldName.substring(0, originalFieldName.lastIndexOf(suffix));
                break;
            }
        }

        if (prefix != null && fieldName.length() > prefix.length()) {
            return StringUtils.uncapitalize(fieldName.substring(prefix.length()));
        }

        return fieldName;
    }

    private Restriction createAttributeRestriction(final Class<?> attributeType, final String attributeName, final String path, final Object value, final boolean isPluralAttribute) {
        final boolean isRangeSearchSupported = isRangeSearchSupported(attributeType);
        final SearchOperator resolvedOperator = resolveFromSearchConfiguration(searchConfiguration, path, attributeType);
        final SearchPropertyConfiguration searchPropertyConfiguration = searchConfiguration.getSearchPropertyConfiguration();

        SearchOperator operator = DefaultSearchOperator.EQ;
        if (resolvedOperator != null) {
            operator = resolvedOperator;
        }
        else if (Collection.class.isAssignableFrom(value.getClass())) {
            operator = DefaultSearchOperator.IN;
        }
        else if (String.class.isAssignableFrom(attributeType)) {
            operator = DefaultSearchOperator.ILIKE;
        }
        else if (isRangeSearchSupported) {
            if (attributeName.endsWith(searchPropertyConfiguration.getRangeQueryFromIncludingSuffix())) {
                operator = DefaultSearchOperator.GE;
            }
            else if (attributeName.endsWith(searchPropertyConfiguration.getRangeQueryFromSuffix())) {
                operator = DefaultSearchOperator.GT;
            }
            else if (attributeName.endsWith(searchPropertyConfiguration.getRangeQueryToIncludingSuffix())) {
                operator = DefaultSearchOperator.LE;
            }
            else if (attributeName.endsWith(searchPropertyConfiguration.getRangeQueryToSuffix())) {
                operator = DefaultSearchOperator.LT;
            }
        }

        return new Restriction(path, operator, value, isPluralAttribute);
    }

    private boolean isRangeSearchSupported(final Class<?> attributeType) {
        return searchConfiguration.getSearchPropertyConfiguration().getRangeQuerySupportedClassList() != null && searchConfiguration.getSearchPropertyConfiguration().getRangeQuerySupportedClassList().stream().anyMatch(type -> type.isAssignableFrom(attributeType));
    }

    private String findPathUsingMapping(final List<SearchPropertyMapping> propertyMappingList, final String fieldName) {
        return Optional.ofNullable(propertyMappingList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(mapping -> fieldName.equals(mapping.getName()))
                .map(SearchPropertyMapping::getPath)
                .findAny()
                .orElse(null);
    }

    private String findPathUsingAttributePrefix(final String originalFieldName, final ManagedType<?> managedType) {
        final List<String> attributeNameList = managedType.getAttributes().stream()
                .filter(Attribute::isAssociation)
                .map(Attribute::getName)
                .collect(Collectors.toList());

        return attributeNameList.stream()
                .filter(attribute -> isFieldNameValid(originalFieldName, attribute))
                .map(attribute -> attribute + "." + StringUtils.uncapitalize(originalFieldName.substring(attribute.length())))
                .findFirst()
                .orElse(null);
    }

    private boolean searchUsingPropertyMapping(final SearchDataParserConfiguration searchConfiguration) {
        return searchConfiguration.isResolveFieldMappingUsingPrefix() || searchConfiguration.getPropertyMappingList() != null;
    }

    private SearchOperator resolveFromSearchConfiguration(final SearchDataParserConfiguration searchConfiguration, final String path, final Class<?> attributeType) {
        SearchOperator operator = null;
        SearchOperatorOverride operatorOverride = findOperatorOverride(searchConfiguration.getSearchOperatorOverrideList(), value -> path.equals(value.getPropertyPath()));

        if (operatorOverride == null) {
            operatorOverride = findOperatorOverride(searchConfiguration.getSearchOperatorOverrideList(), value -> attributeType.isAssignableFrom(value.getPropertyType()));
        }

        if (operatorOverride != null) {
            operator = operatorOverride.getSearchOperator();
        }

        return operator;
    }

    private SearchOperatorOverride findOperatorOverride(final List<SearchOperatorOverride> searchOperatorOverrideList, final Predicate<SearchOperatorOverride> searchOperatorOverridePredicate) {
        return Optional.ofNullable(searchOperatorOverrideList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(searchOperatorOverridePredicate)
                .findFirst()
                .orElse(null);
    }

    private boolean isFieldNameValid(final String fieldName, final String attribute) {
        return fieldName.startsWith(attribute) && fieldName.length() > attribute.length();
    }
}

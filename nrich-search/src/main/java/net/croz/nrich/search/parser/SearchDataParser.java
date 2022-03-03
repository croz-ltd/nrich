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

    private static final String PATH_FORMAT = "%s.%s";

    private final ManagedType<?> managedType;

    private final Object searchData;

    private final SearchDataParserConfiguration searchConfiguration;

    public Set<Restriction> resolveRestrictionList() {
        return resolveRestrictionList(null);
    }

    public Set<Restriction> resolveRestrictionList(String propertyPrefix) {
        return resolveRestrictionListInternal(new MapSupportingDirectFieldAccessFallbackBeanWrapper(searchData), propertyPrefix, null, managedType, new HashSet<>(), false);
    }

    private Set<Restriction> resolveRestrictionListInternal(MapSupportingDirectFieldAccessFallbackBeanWrapper wrapper, String propertyPrefix, String path,
                                                            ManagedType<?> managedType, Set<Restriction> restrictionList, boolean isPluralAttribute) {
        List<String> fieldNameList = resolveFieldNameList(wrapper);
        JpaEntityAttributeResolver attributeResolver = new JpaEntityAttributeResolver(managedType);

        fieldNameList.forEach(originalFieldName -> {
            String fieldNameWithoutPrefixAndSuffix = fieldNameWithoutSuffixAndPrefix(originalFieldName, propertyPrefix);
            Object value = wrapper.getPropertyValue(originalFieldName);

            if (value == null) {
                return;
            }

            AttributeHolder attributeHolder = attributeResolver.resolveAttributeByPath(fieldNameWithoutPrefixAndSuffix);

            if (attributeHolder.getAttribute() != null) {
                String currentPath = resolveCurrentPath(path, fieldNameWithoutPrefixAndSuffix);

                if (attributeHolder.getManagedType() != null) {
                    MapSupportingDirectFieldAccessFallbackBeanWrapper currentWrapper = new MapSupportingDirectFieldAccessFallbackBeanWrapper(value);

                    resolveRestrictionListInternal(currentWrapper, propertyPrefix, currentPath, attributeHolder.getManagedType(), restrictionList, attributeHolder.isPlural());
                    return;
                }

                restrictionList.add(createAttributeRestriction(attributeHolder.getAttribute().getJavaType(), originalFieldName, currentPath, value, isPluralAttribute));
            }
            else if (searchUsingPropertyMapping(searchConfiguration)) {
                String mappedPath = resolveAttributeHolderFromSecurityConfiguration(originalFieldName);

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

    private List<String> resolveFieldNameList(MapSupportingDirectFieldAccessFallbackBeanWrapper wrapper) {
        List<String> configuredIgnoredFieldList = searchConfiguration.getSearchPropertyConfiguration().getSearchIgnoredPropertyList();
        List<String> ignoredFieldList = configuredIgnoredFieldList == null ? Collections.emptyList() : configuredIgnoredFieldList;

        if (wrapper.getEntityAsMap() != null) {
            return wrapper.getEntityAsMap().keySet().stream()
                .filter(key -> !ignoredFieldList.contains(key))
                .collect(Collectors.toList());
        }

        Predicate<Field> shouldIncludeField = field -> !(ignoredFieldList.contains(field.getName()) || field.isSynthetic()
            || Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()));

        return Arrays.stream(wrapper.getRootClass().getDeclaredFields())
            .filter(shouldIncludeField)
            .map(Field::getName)
            .collect(Collectors.toList());
    }

    private String fieldNameWithoutSuffixAndPrefix(String originalFieldName, String prefix) {
        SearchPropertyConfiguration searchPropertyConfiguration = searchConfiguration.getSearchPropertyConfiguration();
        String[] suffixListToRemove = new String[] {
            searchPropertyConfiguration.getRangeQueryFromIncludingSuffix(), searchPropertyConfiguration.getRangeQueryFromSuffix(), searchPropertyConfiguration.getRangeQueryToIncludingSuffix(),
            searchPropertyConfiguration.getRangeQueryToSuffix(), searchPropertyConfiguration.getCollectionQuerySuffix()
        };
        String fieldName = originalFieldName;
        for (String suffix : suffixListToRemove) {
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

    private Restriction createAttributeRestriction(Class<?> attributeType, String attributeName, String path, Object value, boolean isPluralAttribute) {
        boolean isRangeSearchSupported = isRangeSearchSupported(attributeType);
        SearchOperator resolvedOperator = resolveFromSearchConfiguration(searchConfiguration, path, attributeType);
        SearchPropertyConfiguration searchPropertyConfiguration = searchConfiguration.getSearchPropertyConfiguration();

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

    private boolean isRangeSearchSupported(Class<?> attributeType) {
        return searchConfiguration.getSearchPropertyConfiguration().getRangeQuerySupportedClassList() != null
            && searchConfiguration.getSearchPropertyConfiguration().getRangeQuerySupportedClassList().stream().anyMatch(type -> type.isAssignableFrom(attributeType));
    }

    private String resolveCurrentPath(String path, String fieldNameWithoutPrefixAndSuffix) {
        return path == null ? fieldNameWithoutPrefixAndSuffix : String.format(PATH_FORMAT, path, fieldNameWithoutPrefixAndSuffix);
    }

    private String resolveAttributeHolderFromSecurityConfiguration(String originalFieldName) {
        String mappedPath = findPathUsingMapping(searchConfiguration.getPropertyMappingList(), originalFieldName);

        if (mappedPath == null) {
            mappedPath = findPathUsingAttributePrefix(originalFieldName, managedType);
        }

        return mappedPath;
    }

    private String findPathUsingMapping(List<SearchPropertyMapping> propertyMappingList, String fieldName) {
        return Optional.ofNullable(propertyMappingList)
            .orElse(Collections.emptyList())
            .stream()
            .filter(mapping -> fieldName.equals(mapping.getName()))
            .map(SearchPropertyMapping::getPath)
            .findAny()
            .orElse(null);
    }

    private String findPathUsingAttributePrefix(String originalFieldName, ManagedType<?> managedType) {
        List<String> attributeNameList = managedType.getAttributes().stream()
            .filter(attribute -> attribute.isAssociation() || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED)
            .map(Attribute::getName)
            .collect(Collectors.toList());

        return attributeNameList.stream()
            .filter(attribute -> isFieldNameValid(originalFieldName, attribute))
            .map(attribute -> String.format(PATH_FORMAT, attribute, StringUtils.uncapitalize(originalFieldName.substring(attribute.length()))))
            .findFirst()
            .orElse(null);
    }

    private boolean searchUsingPropertyMapping(SearchDataParserConfiguration searchConfiguration) {
        return searchConfiguration.isResolvePropertyMappingUsingPrefix() || searchConfiguration.getPropertyMappingList() != null;
    }

    private SearchOperator resolveFromSearchConfiguration(SearchDataParserConfiguration searchConfiguration, String path, Class<?> attributeType) {
        SearchOperator operator = null;
        SearchOperatorOverride operatorOverride = findOperatorOverride(searchConfiguration.getSearchOperatorOverrideList(), value -> path.equals(value.getPropertyPath()));

        if (operatorOverride == null) {
            Predicate<SearchOperatorOverride> operatorOverridePredicate = value -> value.getPropertyType() != null && attributeType.isAssignableFrom(value.getPropertyType());

            operatorOverride = findOperatorOverride(searchConfiguration.getSearchOperatorOverrideList(), operatorOverridePredicate);
        }

        if (operatorOverride != null) {
            operator = operatorOverride.getSearchOperator();
        }

        return operator;
    }

    private SearchOperatorOverride findOperatorOverride(List<SearchOperatorOverride> searchOperatorOverrideList, Predicate<SearchOperatorOverride> searchOperatorOverridePredicate) {
        return Optional.ofNullable(searchOperatorOverrideList)
            .orElse(Collections.emptyList())
            .stream()
            .filter(searchOperatorOverridePredicate)
            .findFirst()
            .orElse(null);
    }

    private boolean isFieldNameValid(String fieldName, String attribute) {
        return fieldName.startsWith(attribute) && fieldName.length() > attribute.length();
    }
}

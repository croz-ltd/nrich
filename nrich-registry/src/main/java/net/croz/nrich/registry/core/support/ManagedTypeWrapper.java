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

package net.croz.nrich.registry.core.support;

import lombok.Getter;
import net.croz.nrich.registry.core.constants.RegistryCoreConstants;
import org.springframework.util.Assert;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EmbeddableType;
import jakarta.persistence.metamodel.IdentifiableType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class ManagedTypeWrapper {

    private final IdentifiableType<?> identifiableType;

    private final boolean isIdClassIdentifier;

    private final boolean isEmbeddedIdentifier;

    private final Map<String, Class<?>> idClassPropertyMap;

    private final List<String> idClassPropertyNameList;

    private final String idAttributeName;

    private final boolean isIdentifierAssigned;

    private final List<SingularAssociation> singularAssociationList;

    private final List<SingularAssociation> singularEmbeddedTypeAssociationList;

    private final EmbeddableType<?> embeddableIdType;

    public ManagedTypeWrapper(ManagedType<?> managedType) {
        Assert.isTrue(managedType instanceof IdentifiableType, "Managed type has no id attribute, no operations will be possible!");

        identifiableType = (IdentifiableType<?>) managedType;
        embeddableIdType = resolveEmbeddedIdentifierType(identifiableType);
        isEmbeddedIdentifier = embeddableIdType != null;
        isIdClassIdentifier = !identifiableType.hasSingleIdAttribute();
        idAttributeName = resolveIdAttributeName(identifiableType);
        idClassPropertyMap = resolveIdClassPropertyMap(identifiableType);
        idClassPropertyNameList = new ArrayList<>(idClassPropertyMap.keySet());
        isIdentifierAssigned = resolveIsIdentifierAssigned(identifiableType);
        singularAssociationList = resolveSingularAssociationList(identifiableType);
        singularEmbeddedTypeAssociationList = isEmbeddedIdentifier ? resolveSingularAssociationList(embeddableIdType) : Collections.emptyList();
    }

    public Class<?> getJavaType() {
        return identifiableType.getJavaType();
    }

    private EmbeddableType<?> resolveEmbeddedIdentifierType(IdentifiableType<?> identifiableType) {
        return identifiableType.getIdType() instanceof EmbeddableType<?> embeddableType ? embeddableType : null;
    }

    private String resolveIdAttributeName(IdentifiableType<?> identifiableType) {
        return identifiableType.hasSingleIdAttribute() ? identifiableType.getId(identifiableType.getIdType().getJavaType()).getName() : null;
    }

    private Map<String, Class<?>> resolveIdClassPropertyMap(IdentifiableType<?> identifiableType) {
        return identifiableType.hasSingleIdAttribute() ? Collections.emptyMap() : identifiableType.getIdClassAttributes().stream()
            .collect(Collectors.toMap(Attribute::getName, Attribute::getJavaType));
    }

    private boolean resolveIsIdentifierAssigned(IdentifiableType<?> managedType) {
        return managedType.getAttributes().stream()
            .map(Attribute::getJavaMember)
            .filter(Field.class::isInstance)
            .map(Field.class::cast)
            .map(Field::getDeclaredAnnotations)
            .noneMatch(annotationList -> Arrays.stream(annotationList).anyMatch(annotation -> GeneratedValue.class.equals(annotation.annotationType())));
    }

    private List<SingularAssociation> resolveSingularAssociationList(ManagedType<?> managedType) {
        Map<String, SingularAssociation> associationMap = new HashMap<>();

        resolveSingularAssociationList(managedType, null, null, associationMap);

        return new ArrayList<>(associationMap.values());
    }

    private void resolveSingularAssociationList(ManagedType<?> managedType, Boolean isCurrentAssociationPathOptional, String currentPrefix, Map<String, SingularAssociation> singularAssociationMap) {
        @SuppressWarnings("unchecked")
        List<SingularAttribute<?, ?>> currentAssociations = (List<SingularAttribute<?, ?>>) managedType.getSingularAttributes().stream()
            .filter(Attribute::isAssociation)
            .toList();

        for (SingularAttribute<?, ?> association : currentAssociations) {
            String associationName = currentPrefix == null ? association.getName() : String.format(RegistryCoreConstants.PREFIX_FORMAT, currentPrefix, association.getName());
            boolean isCurrentPathOptional = isCurrentAssociationPathOptional == null ? association.isOptional() : isCurrentAssociationPathOptional || association.isOptional();

            singularAssociationMap.put(associationName, new SingularAssociation(associationName, isCurrentPathOptional));
            if (currentPrefix != null) {
                singularAssociationMap.remove(currentPrefix);
            }

            if (!association.getJavaType().equals(managedType.getJavaType())) {
                resolveSingularAssociationList((ManagedType<?>) association.getType(), association.isOptional(), associationName, singularAssociationMap);
            }
        }
    }
}

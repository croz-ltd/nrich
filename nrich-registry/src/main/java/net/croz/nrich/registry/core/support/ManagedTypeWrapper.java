/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private final List<SingularAttribute<?, ?>> singularAssociationList;

    private final List<SingularAttribute<?, ?>> singularEmbeddedTypeAssociationList;

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
        return identifiableType.getIdType() instanceof EmbeddableType ? (EmbeddableType<?>) identifiableType.getIdType() : null;
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

    private List<SingularAttribute<?, ?>> resolveSingularAssociationList(ManagedType<?> managedType) {
        return managedType.getSingularAttributes().stream()
            .filter(Attribute::isAssociation).collect(Collectors.toList());
    }
}

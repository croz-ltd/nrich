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

package net.croz.nrich.search.aot;

import net.croz.nrich.search.repository.JpaSearchExecutor;
import net.croz.nrich.search.repository.JpaStringSearchExecutor;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

import java.util.Collections;
import java.util.List;

public class SearchRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    public static final List<TypeReference> TYPE_REFERENCE_LIST = Collections.unmodifiableList(TypeReference.listOf(JpaSearchExecutor.class, JpaStringSearchExecutor.class));

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerTypes(TYPE_REFERENCE_LIST, hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS));
    }
}

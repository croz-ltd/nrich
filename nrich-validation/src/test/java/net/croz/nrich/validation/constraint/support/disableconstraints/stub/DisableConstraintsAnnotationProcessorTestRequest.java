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

package net.croz.nrich.validation.constraint.support.disableconstraints.stub;

import net.croz.nrich.validation.api.constraint.DisableConstraints;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@DisableConstraints(NotNull.class)
@DisableConstraints(value = NotNull.class, propertyName = "age")
public class DisableConstraintsAnnotationProcessorTestRequest {

    @SuppressWarnings("unused")
    @DisableConstraints(Min.class)
    private Integer employmentDuration;

    @DisableConstraints({ NotBlank.class, Size.class })
    public String getName() {
        return "name";
    }
}

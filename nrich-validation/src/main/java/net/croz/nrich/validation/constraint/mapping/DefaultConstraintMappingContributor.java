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

package net.croz.nrich.validation.constraint.mapping;

import net.croz.nrich.validation.api.constraint.InList;
import net.croz.nrich.validation.api.constraint.MaxSizeInBytes;
import net.croz.nrich.validation.api.constraint.NotNullWhen;
import net.croz.nrich.validation.api.constraint.NullWhen;
import net.croz.nrich.validation.api.constraint.ValidFile;
import net.croz.nrich.validation.api.constraint.ValidFileResolvable;
import net.croz.nrich.validation.api.constraint.ValidOib;
import net.croz.nrich.validation.api.constraint.ValidRange;
import net.croz.nrich.validation.api.constraint.ValidSearchProperties;
import net.croz.nrich.validation.constraint.validator.InListValidator;
import net.croz.nrich.validation.constraint.validator.MaxSizeInBytesValidator;
import net.croz.nrich.validation.constraint.validator.NotNullWhenValidator;
import net.croz.nrich.validation.constraint.validator.NullWhenValidator;
import net.croz.nrich.validation.constraint.validator.ValidFileResolvableValidator;
import net.croz.nrich.validation.constraint.validator.ValidFileValidator;
import net.croz.nrich.validation.constraint.validator.ValidOibValidator;
import net.croz.nrich.validation.constraint.validator.ValidRangeValidator;
import net.croz.nrich.validation.constraint.validator.ValidSearchPropertiesValidator;
import org.hibernate.validator.spi.cfg.ConstraintMappingContributor;

public class DefaultConstraintMappingContributor implements ConstraintMappingContributor {

    @Override
    public void createConstraintMappings(ConstraintMappingBuilder builder) {
        builder.addConstraintMapping().constraintDefinition(ValidOib.class).validatedBy(ValidOibValidator.class);
        builder.addConstraintMapping().constraintDefinition(ValidSearchProperties.class).validatedBy(ValidSearchPropertiesValidator.class);
        builder.addConstraintMapping().constraintDefinition(ValidRange.class).validatedBy(ValidRangeValidator.class);
        builder.addConstraintMapping().constraintDefinition(MaxSizeInBytes.class).validatedBy(MaxSizeInBytesValidator.class);
        builder.addConstraintMapping().constraintDefinition(NotNullWhen.class).validatedBy(NotNullWhenValidator.class);
        builder.addConstraintMapping().constraintDefinition(NullWhen.class).validatedBy(NullWhenValidator.class);
        builder.addConstraintMapping().constraintDefinition(ValidFile.class).validatedBy(ValidFileValidator.class);
        builder.addConstraintMapping().constraintDefinition(ValidFileResolvable.class).validatedBy(ValidFileResolvableValidator.class);
        builder.addConstraintMapping().constraintDefinition(InList.class).validatedBy(InListValidator.class);
    }
}

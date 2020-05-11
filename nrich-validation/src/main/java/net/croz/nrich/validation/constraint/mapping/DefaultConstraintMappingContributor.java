package net.croz.nrich.validation.constraint.mapping;

import net.croz.nrich.validation.api.constraint.MaxSizeInBytes;
import net.croz.nrich.validation.api.constraint.NotNullWhen;
import net.croz.nrich.validation.api.constraint.NullWhen;
import net.croz.nrich.validation.api.constraint.ValidFile;
import net.croz.nrich.validation.api.constraint.ValidOib;
import net.croz.nrich.validation.api.constraint.ValidRange;
import net.croz.nrich.validation.api.constraint.ValidSearchProperties;
import net.croz.nrich.validation.constraint.validator.MaxSizeInBytesValidator;
import net.croz.nrich.validation.constraint.validator.NotNullWhenValidator;
import net.croz.nrich.validation.constraint.validator.NullWhenValidator;
import net.croz.nrich.validation.constraint.validator.ValidFileValidator;
import net.croz.nrich.validation.constraint.validator.ValidOibValidator;
import net.croz.nrich.validation.constraint.validator.ValidRangeValidator;
import net.croz.nrich.validation.constraint.validator.ValidSearchPropertiesValidator;
import org.hibernate.validator.spi.cfg.ConstraintMappingContributor;

public class DefaultConstraintMappingContributor implements ConstraintMappingContributor {

    @Override
    public void createConstraintMappings(final ConstraintMappingBuilder builder) {
        builder.addConstraintMapping().constraintDefinition(ValidOib.class).validatedBy(ValidOibValidator.class);
        builder.addConstraintMapping().constraintDefinition(ValidSearchProperties.class).validatedBy(ValidSearchPropertiesValidator.class);
        builder.addConstraintMapping().constraintDefinition(ValidRange.class).validatedBy(ValidRangeValidator.class);
        builder.addConstraintMapping().constraintDefinition(MaxSizeInBytes.class).validatedBy(MaxSizeInBytesValidator.class);
        builder.addConstraintMapping().constraintDefinition(NotNullWhen.class).validatedBy(NotNullWhenValidator.class);
        builder.addConstraintMapping().constraintDefinition(NullWhen.class).validatedBy(NullWhenValidator.class);
        builder.addConstraintMapping().constraintDefinition(ValidFile.class).validatedBy(ValidFileValidator.class);
    }
}

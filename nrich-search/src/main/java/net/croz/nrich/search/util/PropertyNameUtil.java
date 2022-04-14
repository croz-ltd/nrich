package net.croz.nrich.search.util;

import net.croz.nrich.search.api.model.property.SearchPropertyConfiguration;

public final class PropertyNameUtil {

    private PropertyNameUtil() {
    }

    public static String propertyNameWithoutSuffix(String originalPropertyName, SearchPropertyConfiguration searchPropertyConfiguration) {
        String[] suffixListToRemove = new String[] {
            searchPropertyConfiguration.getRangeQueryFromIncludingSuffix(), searchPropertyConfiguration.getRangeQueryFromSuffix(), searchPropertyConfiguration.getRangeQueryToIncludingSuffix(),
            searchPropertyConfiguration.getRangeQueryToSuffix(), searchPropertyConfiguration.getCollectionQuerySuffix()
        };

        String propertyName = originalPropertyName;
        for (String suffix : suffixListToRemove) {
            if (originalPropertyName.endsWith(suffix)) {
                propertyName = originalPropertyName.substring(0, originalPropertyName.lastIndexOf(suffix));
                break;
            }
        }

        return propertyName;
    }
}

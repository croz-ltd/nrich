package net.croz.nrich.search.api.model;

// TODO support IN and count maybe?

/**
 * Decides if join or exists subquery will be performed for plural associations (default is subquery).
 */
public enum PluralAssociationRestrictionType {
    JOIN, EXISTS
}

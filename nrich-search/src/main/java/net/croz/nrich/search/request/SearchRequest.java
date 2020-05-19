package net.croz.nrich.search.request;

import net.croz.nrich.search.model.SearchConfiguration;

public interface SearchRequest<T, S extends SearchRequest<T, S>> {

    SearchConfiguration<T, S> getSearchConfiguration();

}

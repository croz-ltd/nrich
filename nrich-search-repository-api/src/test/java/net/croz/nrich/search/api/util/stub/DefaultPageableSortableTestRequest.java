package net.croz.nrich.search.api.util.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.api.model.sort.SortProperty;
import net.croz.nrich.search.api.request.SortablePageableRequest;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class DefaultPageableSortableTestRequest implements SortablePageableRequest {

    private final Integer pageNumber;

    private final Integer pageSize;

    private final List<SortProperty> sortPropertyList;

}

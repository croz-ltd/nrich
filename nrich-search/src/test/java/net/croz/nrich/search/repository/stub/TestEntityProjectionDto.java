package net.croz.nrich.search.repository.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.api.annotation.Projection;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
@Getter
public class TestEntityProjectionDto {

    private final String name;

    @Projection(path = "nestedEntity.nestedEntityName")
    private final String nestedName;

    @Value("nestedEntity.id")
    private final Long nestedId;

}

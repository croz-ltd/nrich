package net.croz.nrich.search.repository.stub;

import lombok.Value;
import net.croz.nrich.search.api.annotation.Projection;

@Value
public class TestEntityProjectionDto {

    String name;

    @Projection(path = "nestedEntity.nestedEntityName")
    String nestedName;

    @org.springframework.beans.factory.annotation.Value("nestedEntity.id")
    Long nestedId;

}

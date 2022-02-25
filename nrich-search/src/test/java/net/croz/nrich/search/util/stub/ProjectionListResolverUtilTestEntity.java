package net.croz.nrich.search.util.stub;

import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.annotation.Projection;
import org.springframework.beans.factory.annotation.Value;

import java.util.function.Predicate;

@Setter
@Getter
public class ProjectionListResolverUtilTestEntity {

    private static String STATIC_PROPERTY = "value";

    private String name;

    private transient String transientProperty;

    @Projection(path = "nestedEntity.nestedEntityName")
    private String nestedName;

    @Value("nestedEntity.id")
    private Long nestedId;

    @Projection(path = "nestedEntity.anotherName", condition = Condition.class)
    private String conditionalName;

    public static class Condition implements Predicate<ProjectionListResolverUtilTestEntity> {
        @Override
        public boolean test(ProjectionListResolverUtilTestEntity entity) {
            return entity == null;
        }
    }

}

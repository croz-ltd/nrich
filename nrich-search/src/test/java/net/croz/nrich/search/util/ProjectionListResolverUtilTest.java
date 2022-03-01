package net.croz.nrich.search.util;

import net.croz.nrich.search.api.model.SearchProjection;
import net.croz.nrich.search.util.stub.ProjectionListResolverUtilTestEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ProjectionListResolverUtilTest {

    @Test
    void shouldNotFailWhenNoAnnotationsAreAvailable() {
        // when
        Throwable thrown = catchThrowable(() -> ProjectionListResolverUtil.resolveSearchProjectionList(String.class));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldResolveSearchProjectionList() {
        // when
        List<SearchProjection<ProjectionListResolverUtilTestEntity>> result = ProjectionListResolverUtil.resolveSearchProjectionList(ProjectionListResolverUtilTestEntity.class);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).extracting("path").containsExactly("name", "nestedEntity.nestedEntityName", "nestedEntity.id", "nestedEntity.anotherName");
        assertThat(result).extracting("alias").containsExactly("name", "nestedName", "nestedId", "conditionalName");

        // and when
        SearchProjection<ProjectionListResolverUtilTestEntity> conditionalProjection = result.stream().filter(projection -> "conditionalName".equals(projection.getAlias())).findFirst().orElse(null);

        // then
        assertThat(conditionalProjection).isNotNull();
        assertThat(conditionalProjection.getCondition().test(null)).isTrue();
        assertThat(conditionalProjection.getCondition().test(new ProjectionListResolverUtilTestEntity())).isFalse();
    }
}

package net.croz.nrich.validation.constraint.validator

import net.croz.nrich.validation.ValidationTestConfiguration
import net.croz.nrich.validation.constraint.stub.NotNullWhenWithGroovyClosureTestRequest
import net.croz.nrich.validation.constraint.util.GroovyUtil
import org.junit.jupiter.api.Test
import org.mockito.MockedStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.catchThrowable
import static org.mockito.Mockito.mockStatic

@SpringJUnitConfig(ValidationTestConfiguration)
class NotNullWhenValidatorWithGroovyClosureTest {

  @Autowired
  private Validator validator

  @Test
  void shouldNotReportErrorWhenPropertyIsNullAndConditionIsFalse() {
    // given
    NotNullWhenWithGroovyClosureTestRequest request = new NotNullWhenWithGroovyClosureTestRequest(property: null, differentProperty: "different property")

    // when
    Set<ConstraintViolation<NotNullWhenWithGroovyClosureTestRequest>> constraintViolationList = validator.validate(request)

    // then
    assertThat(constraintViolationList).isEmpty()
  }

  @Test
  void shouldNotReportErrorWhenPropertyIsNotNullAndConditionIsTrue() {
    // given
    NotNullWhenWithGroovyClosureTestRequest request = new NotNullWhenWithGroovyClosureTestRequest(property: "value", differentProperty: "not null")

    // when
    Set<ConstraintViolation<NotNullWhenWithGroovyClosureTestRequest>> constraintViolationList = validator.validate(request)

    // then
    assertThat(constraintViolationList).isEmpty()
  }

  @Test
  void shouldReportErrorWhenPropertyIsNullAndConditionIsTrue() {
    // given
    NotNullWhenWithGroovyClosureTestRequest request = new NotNullWhenWithGroovyClosureTestRequest(property: null, differentProperty: "not null")

    // when
    Set<ConstraintViolation<NotNullWhenWithGroovyClosureTestRequest>> constraintViolationList = validator.validate(request)

    // then
    assertThat(constraintViolationList).isNotEmpty()
  }

  @Test
  void shouldThrowExceptionIfGroovyIsNotPresentWhenConditionIsGroovyClosure() {
    // given
    MockedStatic<GroovyUtil> groovyUtilMock = mockStatic(GroovyUtil)
    groovyUtilMock.when(GroovyUtil::isGroovyPresent).thenReturn(false)

    // when
    Throwable thrown = catchThrowable(() -> validator.validate(new NotNullWhenWithGroovyClosureTestRequest()))

    // then
    assertThat(thrown).isNotNull()

    // cleanup
    groovyUtilMock.close()
  }
}

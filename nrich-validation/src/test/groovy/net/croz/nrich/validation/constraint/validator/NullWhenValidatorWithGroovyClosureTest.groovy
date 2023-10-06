package net.croz.nrich.validation.constraint.validator

import net.croz.nrich.validation.ValidationTestConfiguration
import net.croz.nrich.validation.constraint.stub.NullWhenWithGroovyClosureTestRequest
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
class NullWhenValidatorWithGroovyClosureTest {

  @Autowired
  private Validator validator

  @Test
  void shouldNotReportErrorWhenPropertyIsNotNullAndConditionIsFalse() {
    // given
    NullWhenWithGroovyClosureTestRequest request = new NullWhenWithGroovyClosureTestRequest(property: "value of property", differentProperty: "different property")

    // when
    Set<ConstraintViolation<NullWhenWithGroovyClosureTestRequest>> constraintViolationList = validator.validate(request)

    // then
    assertThat(constraintViolationList).isEmpty()
  }

  @Test
  void shouldNotReportErrorWhenPropertyIsNullAndConditionIsTrue() {
    // given
    NullWhenWithGroovyClosureTestRequest request = new NullWhenWithGroovyClosureTestRequest(property: null, differentProperty: "not null")

    // when
    Set<ConstraintViolation<NullWhenWithGroovyClosureTestRequest>> constraintViolationList = validator.validate(request)

    // then
    assertThat(constraintViolationList).isEmpty()
  }

  @Test
  void shouldReportErrorWhenPropertyIsNotNullAndConditionIsTrue() {
    // given
    NullWhenWithGroovyClosureTestRequest request = new NullWhenWithGroovyClosureTestRequest(property: "value", differentProperty: "not null")

    // when
    Set<ConstraintViolation<NullWhenWithGroovyClosureTestRequest>> constraintViolationList = validator.validate(request)

    // then
    assertThat(constraintViolationList).isNotEmpty()
  }

  @Test
  void shouldThrowExceptionIfGroovyIsNotPresentWhenConditionIsGroovyClosure() {
    // given
    MockedStatic<GroovyUtil> groovyUtilMock = mockStatic(GroovyUtil)
    groovyUtilMock.when(GroovyUtil::isGroovyPresent).thenReturn(false)

    // when
    Throwable thrown = catchThrowable(() -> validator.validate(new NullWhenWithGroovyClosureTestRequest()))

    // then
    assertThat(thrown).isNotNull()

    // cleanup
    groovyUtilMock.close()
  }
}

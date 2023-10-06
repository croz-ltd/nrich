package net.croz.nrich.validation.constraint.util

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.MockedStatic
import org.springframework.util.ClassUtils

import java.util.stream.Stream

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.mockStatic

class GroovyUtilTest {

  @CsvSource(["true,true", "false,false"])
  @ParameterizedTest
  void shouldCheckIfGroovyIsPresent(boolean isMetaClassPresent, boolean expectedResult) {
    // given
    MockedStatic<ClassUtils> classUtilsMock = mockStatic(ClassUtils)
    classUtilsMock.when(() -> ClassUtils.isPresent("groovy.lang.MetaClass", null)).thenReturn(isMetaClassPresent)

    // when
    boolean result = GroovyUtil.isGroovyPresent()

    // then
    assertThat(result).isEqualTo(expectedResult)

    // cleanup
    classUtilsMock.close()
  }


  @MethodSource("shouldCheckIfGivenClassIsGroovyClosureMethodSource")
  @ParameterizedTest
  void shouldCheckIfGivenClassIsGroovyClosure(Class<?> type, boolean expectedResult) {
    // when
    boolean result = GroovyUtil.isGroovyClosure(type)

    // then
    assertThat(result).isEqualTo(expectedResult)
  }

  private static Stream<Arguments> shouldCheckIfGivenClassIsGroovyClosureMethodSource() {
    Stream.of(
        Arguments.arguments({ String inputString -> inputString.toUpperCase() }.class, true),
        Arguments.arguments(String, false)
    )
  }
}

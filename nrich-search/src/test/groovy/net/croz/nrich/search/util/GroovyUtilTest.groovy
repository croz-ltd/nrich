/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.search.util

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

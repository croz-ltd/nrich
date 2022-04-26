/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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

package net.croz.nrich.validation.constraint.util;

import net.croz.nrich.validation.constraint.stub.ValidationReflectionUtilTestRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationReflectionUtilTest {

    @CsvSource({ "first,getFirst", "second,isSecond" })
    @ParameterizedTest
    void shouldFindGetterMethod(String propertyName, String methodName) {
        // when
        Method result = ValidationReflectionUtil.findGetterMethod(ValidationReflectionUtilTestRequest.class, propertyName);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(methodName);
    }

    @Test
    void shouldInvokeMethod() throws Exception {
        // given
        String value = "value";
        ValidationReflectionUtilTestRequest request = new ValidationReflectionUtilTestRequest();
        Method method = ValidationReflectionUtilTestRequest.class.getMethod("getFirst");

        request.setFirst(value);

        // when
        Object result = ValidationReflectionUtil.invokeMethod(method, request);

        // then
        assertThat(result).isEqualTo(value);
    }
}

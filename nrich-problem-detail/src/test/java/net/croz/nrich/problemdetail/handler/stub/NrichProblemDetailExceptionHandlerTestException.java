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

package net.croz.nrich.problemdetail.handler.stub;

import net.croz.nrich.core.api.exception.ExceptionWithArguments;
import net.croz.nrich.core.api.exception.ExceptionWithMessage;
import net.croz.nrich.core.api.exception.ExceptionWithMessageCode;

public class NrichProblemDetailExceptionHandlerTestException extends RuntimeException implements ExceptionWithMessage, ExceptionWithMessageCode, ExceptionWithArguments {

    public static final String MESSAGE_CODE = "order.not-found";

    public NrichProblemDetailExceptionHandlerTestException() {
        super("Order [ORD-001] not found");
    }

    @Override
    public String getMessageCode() {
        return MESSAGE_CODE;
    }

    @Override
    public Object[] getArgumentList() {
        return new Object[] { "ORD-001" };
    }
}

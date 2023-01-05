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

package net.croz.nrich.core.api.exception;

/**
 * Exception indicating an entity has not been found.
 * Inside the argument list a name and id of the entity can be provided and then a custom message can be resolved using those arguments.
 */
public class EntityNotFoundException extends RuntimeException implements ExceptionWithArguments {

    private final transient Object[] argumentList;

    public EntityNotFoundException(String message, Object... argumentList) {
        super(message);
        this.argumentList = argumentList;
    }

    @Override
    public Object[] getArgumentList() {
        return argumentList;
    }
}

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

package net.croz.nrich.excel.api.model;

/**
 * Holder for variable that will be replaced in excel report template.
 * Variables are defined in template in following form <pre>${variableName}</pre>
 *
 * @param name  Name of template variable to replace.
 * @param value Value of template variable that will replace name.
 */
public record TemplateVariable(String name, String value) {

}

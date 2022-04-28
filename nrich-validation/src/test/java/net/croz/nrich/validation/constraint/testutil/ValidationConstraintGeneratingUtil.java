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

package net.croz.nrich.validation.constraint.testutil;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ValidationConstraintGeneratingUtil {

    private ValidationConstraintGeneratingUtil() {
    }

    public static FilePart filePart(String fileName, MediaType mediaType) {
        FilePart filePart = mock(FilePart.class);
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(mediaType);

        when(filePart.filename()).thenReturn(fileName);
        when(filePart.headers()).thenReturn(headers);

        return filePart;
    }
}

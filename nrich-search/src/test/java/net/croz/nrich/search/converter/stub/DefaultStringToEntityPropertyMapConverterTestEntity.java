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

package net.croz.nrich.search.converter.stub;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
public class DefaultStringToEntityPropertyMapConverterTestEntity {

    @Id
    private Long id;

    private String name;

    private Date date;

    private Integer value;

    @OneToOne
    private DefaultStringToEntityPropertyMapConverterTestNestedEntity nestedEntity;

    @OneToMany
    private List<DefaultStringToEntityPropertyMapConverterTestNestedEntity> nestedEntityList;

}

package net.croz.nrich.registry.data.stub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateRegistryTestEntityRequest {

    private Long id;

    private String name;

    private Integer age;

}

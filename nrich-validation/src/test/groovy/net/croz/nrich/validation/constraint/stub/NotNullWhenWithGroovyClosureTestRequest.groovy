package net.croz.nrich.validation.constraint.stub

import net.croz.nrich.validation.api.constraint.NotNullWhen

@NotNullWhen(property = "property", condition = { notNullWhenTestRequest -> "not null" == notNullWhenTestRequest.differentProperty })
class NotNullWhenWithGroovyClosureTestRequest {

  String property

  String differentProperty
}

package net.croz.nrich.validation.constraint.stub

import net.croz.nrich.validation.api.constraint.NullWhen

@NullWhen(property = "property", condition = { nullWhenTestRequest -> "not null" == nullWhenTestRequest.differentProperty })
class NullWhenWithGroovyClosureTestRequest {

  String property

  String differentProperty
}

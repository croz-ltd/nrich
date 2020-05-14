package net.croz.nrich.webmvc.service.stub;

public class TransientPropertyResolverServiceImplTestRequest extends TransientPropertyResolverServiceImplTestParentRequest {

    private transient int anotherValue;

    private int nonTransientValue;

}

package net.croz.nrich.webmvc.service.stub;

@SuppressWarnings("unused")
public class TransientPropertyResolverServiceImplTestRequest extends TransientPropertyResolverServiceImplTestParentRequest {

    private transient int anotherValue;

    private int nonTransientValue;

}

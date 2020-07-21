package net.croz.nrich.registry.security.testutil;

import net.croz.nrich.registry.api.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.api.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.api.data.request.UpdateRegistryServiceRequest;

public final class RegistrySecurityGeneratingUtil {

    private RegistrySecurityGeneratingUtil() {
    }

    public static CreateRegistryServiceRequest createRegistryServiceRequest(final String classFullName) {
        final CreateRegistryServiceRequest request = new CreateRegistryServiceRequest();

        request.setClassFullName(classFullName);

        return request;
    }

    public static UpdateRegistryServiceRequest updateRegistryServiceRequest(final String classFullName) {
        final UpdateRegistryServiceRequest request = new UpdateRegistryServiceRequest();

        request.setClassFullName(classFullName);

        return request;
    }

    public static DeleteRegistryRequest deleteRegistryRequest(final String classFullName) {
        final DeleteRegistryRequest request = new DeleteRegistryRequest();

        request.setClassFullName(classFullName);

        return request;
    }
}

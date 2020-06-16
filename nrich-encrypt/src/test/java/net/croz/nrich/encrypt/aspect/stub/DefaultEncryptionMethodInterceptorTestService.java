package net.croz.nrich.encrypt.aspect.stub;

public class DefaultEncryptionMethodInterceptorTestService implements EncryptionMethodInterceptorTestService {

    @Override
    public EncryptDataAspectTestServiceResult dataToEncryptFromConfiguration(final String value) {
        return new EncryptDataAspectTestServiceResult(value);
    }

    @Override
    public EncryptDataAspectTestServiceResult ignoredMethod(final String value) {
        return new EncryptDataAspectTestServiceResult(value);
    }

    @Override
    public String dataToDecryptFromConfiguration(final EncryptDataAspectTestServiceResult data) {
        return data.getValue();
    }
}

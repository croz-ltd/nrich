package net.croz.nrich.encrypt.aspect.stub;

public class DefaultEncryptionMethodInterceptorTestService implements EncryptionMethodInterceptorTestService {

    @Override
    public EncryptDataAspectTestServiceResult dataToEncryptFromConfiguration(String value) {
        return new EncryptDataAspectTestServiceResult(value);
    }

    @Override
    public EncryptDataAspectTestServiceResult ignoredMethod(String value) {
        return new EncryptDataAspectTestServiceResult(value);
    }

    @Override
    public String dataToDecryptFromConfiguration(EncryptDataAspectTestServiceResult data) {
        return data.getValue();
    }
}

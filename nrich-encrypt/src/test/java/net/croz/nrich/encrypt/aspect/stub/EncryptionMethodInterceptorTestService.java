package net.croz.nrich.encrypt.aspect.stub;

public interface EncryptionMethodInterceptorTestService {

    EncryptDataAspectTestServiceResult dataToEncryptFromConfiguration(String value);

    String dataToDecryptFromConfiguration(EncryptDataAspectTestServiceResult data);

    EncryptDataAspectTestServiceResult ignoredMethod(String value);

}

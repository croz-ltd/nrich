package net.croz.nrich.encrypt.api.service;

import net.croz.nrich.encrypt.api.model.EncryptionContext;

import java.util.List;

/**
 * Resolves string values to be encrypted/decrypted from data by path list and delegates encryption operation to {@link TextEncryptionService}.
 */
public interface DataEncryptionService {

    /**
     * Returns data with encrypted values matching path list.
     *
     * @param data                     object holding values that will be encrypted (can be a collection)
     * @param pathToEncryptDecryptList list of paths on data object that will be encrypted
     * @param encryptionContext        context in which encryption operation is performed
     * @param <T>                      type of data
     * @return data object with encrypted values
     */
    <T> T encryptData(T data, List<String> pathToEncryptDecryptList, EncryptionContext encryptionContext);

    /**
     * Returns data with decrypted values matching path list.
     *
     * @param data                     object holding values that will be decrypted (can be a collection)
     * @param pathToEncryptDecryptList list of paths on data object that will be decrypted
     * @param encryptionContext        context in which encryption operation is performed
     * @param <T>                      type of data
     * @return data object with decrypted values
     */
    <T> T decryptData(T data, List<String> pathToEncryptDecryptList, EncryptionContext encryptionContext);
}

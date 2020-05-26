package net.croz.nrich.excel.storage;

import java.io.File;

public interface FileStorage {

    File create(String filename);

    File find(String fileName);

    File[] list();
}

package net.croz.nrich.excel.storage;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.IntStream;

@AllArgsConstructor
public class FileSystemFileStorage implements FileStorage {

    private static final String FILE_NAME_FORMAT = "%s-%s.%s";

    private final String directory;

    private final String extension;

    private final Integer maxStoredFiles;

    @SneakyThrows
    @Override
    public File create(final String filename) {
        final File dir = directory();

        ensureDirectoryExists(dir);
        ensureCapacity();

        final File file = new File(dir, appendTimestampToFileName(filename));

        if (!file.createNewFile()) {
            throw new IllegalArgumentException("File creation failed");
        }

        return file;
    }

    @Override
    public File find(final String fileName) {
        return Arrays.stream(Optional.ofNullable(list()).orElse(new File[0])).filter(file -> file.getName().equals(fileName)).findFirst().orElse(null);
    }

    @SneakyThrows
    @Override
    public File[] list() {
        final File dir = directory();

        final File[] fileList = dir.listFiles();

        File[] directoryContent = null;
        if (dir.isDirectory() && fileList != null) {
            directoryContent = Arrays.stream(fileList).filter(file -> file.isFile() && file.getName().endsWith(extension)).toArray(File[]::new);
        }

        return directoryContent;
    }

    private File directory() {
        return new File(directory);
    }

    private void ensureCapacity() {
        final File[] fileList = list();

        if (fileList == null || fileList.length < maxStoredFiles) {
            return;
        }

        Arrays.sort(fileList, Comparator.comparingLong(File::lastModified).reversed());

        final int numberOfFilesToDelete = fileList.length - maxStoredFiles + 1;

        IntStream.range(0, numberOfFilesToDelete).forEach(index -> deleteFile(fileList[index]));
    }

    @SneakyThrows
    private void ensureDirectoryExists(final File dir) {
        if (!dir.exists()) {
            Files.createDirectories(dir.toPath());
        }
    }

    @SneakyThrows
    private void deleteFile(final File file) {
        Files.delete(file.toPath());
    }

    private String appendTimestampToFileName(final String fileName) {
        return String.format(FILE_NAME_FORMAT, fileName, DateTimeFormatter.ofPattern("yyyy-MM-dd-HH_mm_ss_SSS").format(LocalDateTime.now()), extension);
    }
}

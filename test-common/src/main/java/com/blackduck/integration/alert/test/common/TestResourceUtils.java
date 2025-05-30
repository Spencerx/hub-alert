/*
 * blackduck-alert
 *
 * Copyright (c) 2024 Black Duck Software, Inc.
 *
 * Use subject to the terms and conditions of the Black Duck Software End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.blackduck.integration.alert.test.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

public final class TestResourceUtils {
    public static final String DEFAULT_PROPERTIES_FILE_NAME = "test.properties";
    public static final String DEFAULT_SWAGGER_API_SPEC_FILE_NAME = "swagger.api-spec";

    private static final String SUB_PROJECT_NAME = "test-common";
    private static final File EXPECTED_BASE_TEST_RESOURCE_DIR = new File(
        TestResourceUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
        "../../../../"
    );
    private static final File EXPECTED_ROOT_DIR = new File(
        TestResourceUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
        "../../../../../"
    );

    public static Path createSwaggerAPISpecCanonicalFilePath() throws IOException {
        File buildOutputDir = new File(TestResourceUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getCanonicalFile();
        File baseDirectory = findAncestorDirectory(buildOutputDir, SUB_PROJECT_NAME).orElse(EXPECTED_ROOT_DIR);
        return Path.of(baseDirectory.getAbsolutePath(), "build", "swagger", TestResourceUtils.DEFAULT_SWAGGER_API_SPEC_FILE_NAME);
    }

    public static Path createTestPropertiesCanonicalFilePath() throws IOException {
        File buildOutputDir = new File(TestResourceUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getCanonicalFile();
        File subProjectDir = findAncestorDirectory(buildOutputDir, SUB_PROJECT_NAME).orElse(EXPECTED_BASE_TEST_RESOURCE_DIR);
        return Path.of(subProjectDir.getAbsolutePath(), "src", "main", "resources", TestResourceUtils.DEFAULT_PROPERTIES_FILE_NAME);
    }

    public static Optional<File> findAncestorDirectory(File file, String ancestorDirectoryName) {
        while (!ancestorDirectoryName.equals(file.getName())) {
            file = file.getParentFile();
            if (null == file) {
                return Optional.empty();
            }
        }
        return Optional.of(file);
    }

    /**
     * @param resourcePath The path to the file resource. For example: If the file is in src/test/resources/dir1/dir2/file.ext, then use "dir1/dir2/file.ext"
     * @return The file contents, never null
     * @throws IOException Thrown by {@link FileUtils} if the file cannot be read
     */
    public static String readFileToString(String resourcePath) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(resourcePath);
        File jsonFile = classPathResource.getFile();
        return FileUtils.readFileToString(jsonFile, Charset.defaultCharset());
    }

    public static Properties loadProperties(String resourceLocation) throws IOException {
        Properties properties = new Properties();

        ClassPathResource classPathResource = new ClassPathResource(resourceLocation);
        try (InputStream classPathInputStream = classPathResource.getInputStream()) {
            properties.load(classPathInputStream);
            return properties;
        } catch (IOException ioException) {
            System.out.printf("Failed to load [%s] from classpath%n", resourceLocation);
        }

        File fileResource = new File(resourceLocation);
        try (FileInputStream fileInputStream = FileUtils.openInputStream(fileResource)) {
            properties.load(fileInputStream);
            return properties;
        } catch (IOException ioException) {
            System.out.printf("Failed to load [%s] as file%n", resourceLocation);
            throw ioException;
        }
    }

    private TestResourceUtils() {
    }

}

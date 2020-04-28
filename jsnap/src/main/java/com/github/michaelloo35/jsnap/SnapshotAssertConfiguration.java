package com.github.michaelloo35.jsnap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flipkart.zjsonpatch.DiffFlags;

import java.io.File;
import java.util.EnumSet;

import static java.lang.ThreadLocal.withInitial;

public class SnapshotAssertConfiguration {

    private static final ThreadLocal<ObjectMapper> om = withInitial(() -> {
        ObjectMapper objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    });
    static EnumSet<DiffFlags> DIFF_FLAGS = EnumSet.of(DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE);
    private static String snapshotGenerationAbsolutePath = System.getProperty("user.dir");
    private static String resourcesAbsolutePath = "";

    public static ObjectMapper getObjectMapper() {
        return om.get();
    }

    public static String getSnapshotGenerationAbsolutePath() {
        return snapshotGenerationAbsolutePath;
    }

    public static String getResourcesPathUri() {
        return resourcesAbsolutePath;
    }

    public static void setSnapshotGenerationAbsolutePath(String path) {
        SnapshotAssertConfiguration.snapshotGenerationAbsolutePath = path;
    }

    public static void setMavenModuleRelativeResourcesPath(String path) {
        resourcesAbsolutePath = "file:" + File.separator + File.separator + System.getProperty("user.dir") + path;
    }
}


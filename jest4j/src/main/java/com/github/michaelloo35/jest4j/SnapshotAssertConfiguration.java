package com.github.michaelloo35.jest4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.flipkart.zjsonpatch.DiffFlags;

import java.util.EnumSet;

import static java.lang.ThreadLocal.withInitial;

public class SnapshotAssertConfiguration {

    private static final ThreadLocal<ObjectMapper> om = withInitial(() -> {
        ObjectMapper objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new AfterburnerModule());
        return objectMapper;
    });
    static EnumSet<DiffFlags> DIFF_FLAGS = EnumSet.of(DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE);
    static String snapshotGenerationAbsolutePath = System.getProperty("user.dir");

    public static ObjectMapper getObjectMapper() {
        return om.get();
    }

    public static void setSnapshotGenerationAbsolutePath(String path) {
        SnapshotAssertConfiguration.snapshotGenerationAbsolutePath = path;
    }
}


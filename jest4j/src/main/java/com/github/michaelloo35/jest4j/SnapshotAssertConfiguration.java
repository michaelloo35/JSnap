package com.github.michaelloo35.jest4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flipkart.zjsonpatch.DiffFlags;

import java.util.EnumSet;

import static java.lang.ThreadLocal.withInitial;

public class SnapshotAssertConfiguration {

    static EnumSet<DiffFlags> DIFF_FLAGS = EnumSet.of(DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE);
    private static final ThreadLocal<ObjectMapper> om = withInitial(() -> {
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    });

    public static ObjectMapper getObjectMapper() {
        return om.get();
    }
}


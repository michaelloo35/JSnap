package com.github.michaelloo35.jest4j;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

public class SnapshotAssert {

    private static final EnumSet<DiffFlags> DIFF_FLAGS = EnumSet.of(
            DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE);
    private static final String EMPTY_ARRAY_JSON = "[]";
    private final ObjectMapper objectMapper;

    public SnapshotAssert() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
    }

    public SnapshotAssert(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void assertThatMatchesWithSnapshot(Object object, String snapshotName) throws IOException {
        createSnapshotsDirectoryIfMissing();

        File snapshot = new File(getSnapshotsPath() + '/' + snapshotName + ".json");

        if (!snapshot.exists()) {
            createSnapshot(object, snapshot);

            throw new FailAfterInitialSnapshotGenerationException(
                    "\n\nCreated snapshot under:\n" + snapshot.getAbsolutePath() + "\n\nPlease verify the results");
        } else {
            JsonNode expected = objectMapper.readTree(snapshot);
            JsonNode actual = objectMapper.readTree(createEphemeralSnapshot(object));
            JsonNode diff = JsonDiff.asJson(expected, actual, DIFF_FLAGS);

            if (!diff.equals(objectMapper.readTree(EMPTY_ARRAY_JSON))) {
                throw new AssertionFailureException(
                        "\nActual does not match snapshot\n" + snapshot.getAbsolutePath() + "\n\nDifferences:\n" +
                                prettyPrintJsonString(diff));
            }
        }
    }

    public String prettyPrintJsonString(JsonNode jsonNode) {
        try {
            Object json = objectMapper.readValue(jsonNode.toString(), Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            return "Sorry, pretty printing differences didn't work";
        }
    }

    private void createSnapshot(Object object, File snapshot) throws IOException {
        ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(snapshot, object);
    }

    private String createEphemeralSnapshot(Object object) throws IOException {
        ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
        return writer.writeValueAsString(object);
    }

    private void createSnapshotsDirectoryIfMissing() {
        File files = new File(getSnapshotsPath());
        if (!files.exists()) {
            files.mkdir();
        }
    }

    private String getSnapshotsPath() {
        return System.getProperty("user.dir") + "/src/test/snapshots";
    }
}


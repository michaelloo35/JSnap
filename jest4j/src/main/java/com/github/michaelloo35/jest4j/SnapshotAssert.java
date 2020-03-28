package com.github.michaelloo35.jest4j;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import org.assertj.core.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

public class SnapshotAssert {

    private static final EnumSet<DiffFlags> DIFF_FLAGS = EnumSet.of(
            DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE);
    private static final String EMPTY_ARRAY_JSON = "[]";
    private final ObjectMapper objectMapper;

    public SnapshotAssert() {
        this.objectMapper = new ObjectMapper();
    }

    public SnapshotAssert(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void assertThatMatchesWithSnapshot(Object object, String snapshotName) throws IOException {
        createSnapshotsDirectoryIfMissing();

        File snapshot = new File(getSnapshotsPath() + '/' + snapshotName + ".json");

        if (!snapshot.exists()) {
            createSnapshot(object, snapshot);
            Assertions.fail("Created snapshot under " + snapshot.getAbsolutePath() + " please verify its content");
        } else {

            JsonNode expected = objectMapper.readTree(snapshot);
            JsonNode actual = objectMapper.valueToTree(object);
            JsonNode diff = JsonDiff.asJson(expected, actual, DIFF_FLAGS);
            Assertions.assertThat(diff)
                    .withFailMessage(
                            "Actual does not match snapshot\n" + snapshot.getAbsolutePath() + "\n\nDifferences:\n" +
                                    prettyPrintJsonString(diff))
                    .isEqualTo(objectMapper.readTree(EMPTY_ARRAY_JSON));
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


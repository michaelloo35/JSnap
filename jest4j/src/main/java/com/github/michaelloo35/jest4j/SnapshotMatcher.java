package com.github.michaelloo35.jest4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;

import java.io.File;
import java.io.IOException;

import static com.github.michaelloo35.jest4j.SnapshotAssertConfiguration.DIFF_FLAGS;
import static com.github.michaelloo35.jest4j.SnapshotAssertConfiguration.getObjectMapper;

public class SnapshotMatcher implements MatchingStep {

    private static final String EMPTY_ARRAY_JSON = "[]";
    private final Object actual;

    private SnapshotMatcher(Object actual) {
        this.actual = actual;
    }

    public static MatchingStep expect(Object actual) {
        return new SnapshotMatcher(actual);
    }

    @Override
    public void toMatchSnapshot(String uniqueSnapshotFileName) {
        try {
            createSnapshotsDirectoryIfMissing();

            File snapshot = new File(getSnapshotsPath() + '/' + uniqueSnapshotFileName + ".json");

            if (!snapshot.exists()) {
                getObjectMapper().writeValue(snapshot, actual);

                throw new FailAfterInitialSnapshotGenerationException(
                        "\n\nCreated snapshot under:\n" + snapshot.getAbsolutePath() + "\n\nPlease verify the results");
            } else {
                JsonNode expected = getObjectMapper().readTree(snapshot);
                JsonNode ephemeralActual = getObjectMapper().readTree(getObjectMapper().writeValueAsString(actual));
                JsonNode diff = JsonDiff.asJson(expected, ephemeralActual, DIFF_FLAGS);

                if (!diff.equals(getObjectMapper().readTree(EMPTY_ARRAY_JSON))) {
                    throw new AssertionFailureException(
                            "\nActual does not match snapshot\n" + snapshot.getAbsolutePath() + "\n\nDifferences:\n" +
                                    prettyPrintJsonString(diff));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String prettyPrintJsonString(JsonNode jsonNode) {
        try {
            Object json = getObjectMapper().readValue(jsonNode.toString(), Object.class);
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            return "Sorry, pretty printing differences didn't work";
        }
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

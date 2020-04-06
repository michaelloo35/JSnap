package com.github.michaelloo35.jest4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.github.michaelloo35.jest4j.SnapshotAssertConfiguration.DIFF_FLAGS;
import static com.github.michaelloo35.jest4j.SnapshotAssertConfiguration.getObjectMapper;
import static com.github.michaelloo35.jest4j.SnapshotAssertConfiguration.snapshotGenerationLocation;

public class SnapshotMatcher implements MatchingStep {

    private static final String EMPTY_ARRAY_JSON = "[]";
    public static final String RESOURCES_RELATIVE_SNAPSHOTS_DIRECTORY = "snapshots";
    private final Object actual;

    private SnapshotMatcher(Object actual) {
        this.actual = actual;
    }

    public static MatchingStep expect(Object actual) {
        return new SnapshotMatcher(actual);
    }

    @Override
    public void toMatchSnapshot(String uniqueSnapshotFileName) {
        String snapshotFileName = uniqueSnapshotFileName + ".json";
        String resourcesRelativeSnapshotPath = RESOURCES_RELATIVE_SNAPSHOTS_DIRECTORY + '/' + snapshotFileName;

        URL snapshotResource = getClass().getClassLoader().getResource(resourcesRelativeSnapshotPath);
        if (snapshotResource != null) {
            compareWithSnapshot(snapshotFileName, snapshotResource);
        } else {
            createSnapshotFile(snapshotFileName);
        }
    }

    private void compareWithSnapshot(String snapshotFileName, URL snapshotResource) {
        try {
            JsonNode expected = null;
            expected = getObjectMapper().readTree(snapshotResource);

            JsonNode actual = getObjectMapper().readTree(getObjectMapper().writeValueAsString(this.actual));
            JsonNode differences = JsonDiff.asJson(expected, actual, DIFF_FLAGS);

            if (!differences.equals(getObjectMapper().readTree(EMPTY_ARRAY_JSON))) {
                throw new AssertionFailureException(
                        "\nActual does not match snapshot\n" + snapshotFileName +
                                "\n\nDifferences:\n" + getObjectMapper().writeValueAsString(differences));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSnapshotFile(String snapshotFileName) {
        try {
            File generatedSnapshot = new File(snapshotGenerationLocation + snapshotFileName);
            getObjectMapper().writeValue(generatedSnapshot, actual);

            throw new FailAfterInitialSnapshotGenerationException(
                    "\n\nCreated snapshot under:\n" + generatedSnapshot.getAbsolutePath() +
                            "\n\nPlease verify the results and move them to:\n"
                            + RESOURCES_RELATIVE_SNAPSHOTS_DIRECTORY + '/' + snapshotFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

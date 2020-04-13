package com.github.michaelloo35.jest4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import static com.github.michaelloo35.jest4j.SnapshotAssertConfiguration.DIFF_FLAGS;
import static com.github.michaelloo35.jest4j.SnapshotAssertConfiguration.getObjectMapper;
import static com.github.michaelloo35.jest4j.SnapshotAssertConfiguration.snapshotGenerationAbsolutePath;

public class SnapshotMatcher implements MatchingStep {

    private static final String EMPTY_ARRAY_JSON = "[]";
    private final Object actual;

    public static MatchingStep expect(Object actual) {
        return new SnapshotMatcher(actual);
    }

    private SnapshotMatcher(Object actual) {
        this.actual = actual;
    }

    @Override
    public void toMatchSnapshot(String resourcesRelativeSnapshotPath) {
        String resourcesRelativeSnapshotPathWithExtension =
                resourcesRelativeSnapshotPath.endsWith(".json")
                        ? resourcesRelativeSnapshotPath
                        : resourcesRelativeSnapshotPath + ".json";

        String snapshotFileName = Paths.get(resourcesRelativeSnapshotPathWithExtension).getFileName().toString();

        URL snapshotResource = getClass().getClassLoader().getResource(resourcesRelativeSnapshotPathWithExtension);
        if (snapshotResource != null) {
            compareWithExistingSnapshot(snapshotFileName, snapshotResource);
        } else {
            createSnapshotFile(snapshotFileName, resourcesRelativeSnapshotPathWithExtension);
        }
    }

    private void compareWithExistingSnapshot(String snapshotFileName, URL snapshotResource) {
        try {
            JsonNode expected = getObjectMapper().readTree(snapshotResource);
            JsonNode actual = getObjectMapper().readTree(getObjectMapper().writeValueAsString(this.actual));
            JsonNode differences = JsonDiff.asJson(expected, actual, DIFF_FLAGS);

            if (!differences.equals(getObjectMapper().readTree(EMPTY_ARRAY_JSON))) {
                throw new AssertionFailureException(
                        "\nActual does not match expected\n" + snapshotFileName +
                                "\n\nDifferences are:\n" + getObjectMapper().writeValueAsString(differences));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSnapshotFile(String snapshotFileName, String resourcesRelativeSnapshotFilePathWithExtension) {
        try {
            File generatedSnapshot = new File(snapshotGenerationAbsolutePath, snapshotFileName);
            getObjectMapper().writeValue(generatedSnapshot, actual);

            throw new FailAfterInitialSnapshotGenerationException(
                    "\n\nCreated snapshot under:\n" + generatedSnapshot.getAbsolutePath() +
                            "\n\nPlease verify contents of snapshot and move it to path:\n"
                            + resourcesRelativeSnapshotFilePathWithExtension +
                            "\nwhich is relative to your resources directory.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.github.michaelloo35.jsnap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import static com.github.michaelloo35.jsnap.SnapshotAssertConfiguration.DIFF_FLAGS;
import static com.github.michaelloo35.jsnap.SnapshotAssertConfiguration.getObjectMapper;
import static com.github.michaelloo35.jsnap.SnapshotAssertConfiguration.getResourcesPathUri;
import static com.github.michaelloo35.jsnap.SnapshotAssertConfiguration.getSnapshotGenerationAbsolutePath;

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
            compareWithExistingSnapshot(resourcesRelativeSnapshotPathWithExtension, snapshotResource);
        } else {
            createSnapshotFile(snapshotFileName, resourcesRelativeSnapshotPathWithExtension);
        }
    }

    private void compareWithExistingSnapshot(String resourcesRelativeSnapshotPath, URL snapshotResource) {
        try {
            JsonNode expected = getObjectMapper().readTree(snapshotResource);
            JsonNode actual = getObjectMapper().readTree(getObjectMapper().writeValueAsString(this.actual));
            JsonNode differences = JsonDiff.asJson(expected, actual, DIFF_FLAGS);

            if (!differences.equals(getObjectMapper().readTree(EMPTY_ARRAY_JSON))) {
                throw new AssertionFailureException(
                        prepareAssertionsFailureMessage(resourcesRelativeSnapshotPath, differences));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSnapshotFile(String snapshotFileName, String resourcesRelativeSnapshotFilePathWithExtension) {
        try {
            File generatedSnapshot = new File(getSnapshotGenerationAbsolutePath(), snapshotFileName);
            getObjectMapper().writeValue(generatedSnapshot, actual);

            throw new FailAfterInitialSnapshotGenerationException(
                    prepareGeneratedSnapshotMessage(resourcesRelativeSnapshotFilePathWithExtension, generatedSnapshot));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String prepareAssertionsFailureMessage(String resourcesRelativeSnapshotPath, JsonNode differences)
            throws JsonProcessingException {
        return "Returned value does not match snapshot"
                + System.lineSeparator()
                + getResourcesPathUri()
                + resourcesRelativeSnapshotPath
                + DiffPrettyPrinter.print(differences);
    }

    private String prepareGeneratedSnapshotMessage(String resourcesRelativeSnapshotFilePathWithExtension,
            File generatedSnapshot) {
        return System.lineSeparator() + System.lineSeparator()
                + "Created snapshot under:" + System.lineSeparator() + generatedSnapshot.getAbsolutePath()
                + System.lineSeparator() + System.lineSeparator()
                + "Please verify contents of snapshot and move it to path:"
                + System.lineSeparator()
                + resourcesRelativeSnapshotFilePathWithExtension
                + System.lineSeparator()
                + "which is relative to your resources directory.";
    }
}

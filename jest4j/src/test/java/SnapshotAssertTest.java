import com.github.michaelloo35.jest4j.SnapshotAssertConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.NestedObj;
import utils.TestClass;

import java.io.IOException;

import static com.github.michaelloo35.jest4j.SnapshotMatcher.expect;

public class SnapshotAssertTest {

    @BeforeClass
    public void setUp() {
        SnapshotAssertConfiguration
                .setMavenModuleRelativeResourcesPath("/src/test/resources/");
    }

    @Test
    public void shouldCreateInitialSnapshotAndThenTestAgainstIt() throws IOException {

        // given

        TestClass test = new TestClass("ab", 4);
        test.setSomeNumber(new NestedObj(new NestedObj(null)));
        // when

        expect(test).toMatchSnapshot("snapshots/test-snapshot");
        // then

    }
}

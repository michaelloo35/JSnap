import com.github.michaelloo35.jest4j.SnapshotAssertConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.TestClass;

import java.io.IOException;

import static com.github.michaelloo35.jest4j.SnapshotMatcher.expect;

public class SnapshotAssertTest {

    private SnapshotAssertConfiguration snapshotAssert;

    @BeforeClass
    public void setUp() {
        snapshotAssert = new SnapshotAssertConfiguration();
    }

    @Test
    public void shouldCreateInitialSnapshotAndThenTestAgainstIt() throws IOException {

        // given

        TestClass test = new TestClass("ab", 4);
        // when

        expect(test).toMatchSnapshot("test-snapshot");
        // then

    }
}

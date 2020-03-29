import com.github.michaelloo35.jest4j.SnapshotAssert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.TestClass;

import java.io.IOException;

public class SnapshotAssertTest {

    private SnapshotAssert snapshotAssert;

    @BeforeClass
    public void setUp() {
        snapshotAssert = new SnapshotAssert();
    }

    @Test
    public void shouldCreateInitialSnapshotAndThenTestAgainstIt() throws IOException {

        // given

        TestClass test = new TestClass("ab", 4);
        // when

        // then
        snapshotAssert.assertThatMatchesWithSnapshot(test, "test-snapshot-v1");
    }
}

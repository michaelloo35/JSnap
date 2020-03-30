package utils;

import java.time.Instant;
import java.util.Optional;

public class TestClass {

    private Instant time = Instant.ofEpochSecond(1583017200);
    private int someNumber = 4;
    private Optional<String> someString;

    public TestClass(String someString, int someNumber) {
        this.someString = Optional.of("empty");
        this.someNumber = someNumber;
    }

    public int getSomeNumber() {
        return someNumber;
    }

    public Optional<String> getSomeString() {
        return someString;
    }

    public Instant getTime() {
        return time;
    }
}

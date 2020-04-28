package utils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public class TestClass {

    private Instant time = Instant.ofEpochSecond(1583017200);
    private BigDecimal test;
    private Optional<String> someString;
    private NestedObj someNumber;

    public TestClass(String someString, int test) {
        this.someString = Optional.of("empty");
        this.test = BigDecimal.valueOf(test);
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public void setTest(BigDecimal test) {
        this.test = test;
    }

    public void setSomeString(Optional<String> someString) {
        this.someString = someString;
    }

    public void setSomeNumber(NestedObj someNumber) {
        this.someNumber = someNumber;
    }

    public Instant getTime() {
        return time;
    }

    public BigDecimal getTest() {
        return test;
    }

    public Optional<String> getSomeString() {
        return someString;
    }

    public NestedObj getSomeNumber() {
        return someNumber;
    }
}

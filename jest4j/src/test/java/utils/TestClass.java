package utils;

import java.util.Optional;

public class TestClass {

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
}

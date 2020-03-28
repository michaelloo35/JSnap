package utils;

public class TestClass {

    private int someNumber = 4;
    private String someString = "hajbajirg";

    public TestClass(String someString, int someNumber) {
        this.someString = someString;
        this.someNumber = someNumber;
    }

    public int getSomeNumber() {
        return someNumber;
    }

    public String getSomeString() {
        return someString;
    }
}

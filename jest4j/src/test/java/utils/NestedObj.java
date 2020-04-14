package utils;

public class NestedObj {

    private final String testingA = "aaaa";
    private final String testingB = "aaaa";
    private final NestedObj nestedObj;

    public NestedObj(NestedObj nestedObj) {
        this.nestedObj = nestedObj;
    }

    public String getTestingA() {
        return testingA;
    }

    public String getTestingB() {
        return testingB;
    }

    public NestedObj getNestedObj() {
        return nestedObj;
    }
}

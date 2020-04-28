# JSnap
Snapshot testing made for java

* Framework agnostic
* Lightweight
* Verbose
* Simple to use

## How to use it:
1. Import the library
```java
import com.github.michaelloo35.jsnap.SnapshotAssertConfiguration;
```
2. Configure path to resources directory relative to project root.
   For single module maven project it would be:
```java
SnapshotAssertConfiguration.setMavenModuleRelativeResourcesPath("/src/test/resources/");
```
3. Write some assertions
```java
expect(actual).toMatchSnapshot("snapshotFileNameGoesHere");
```
After running the test for the first time snapshot file will be generated in current path:
<insert-gif-here>

This file should be verified and moved to resources folder pointed before (#2).

From now on the test will assert actual against the snapshot and point out any differences or pass if there are any.


### Maven release
[![](https://jitpack.io/v/michaelloo35/Jest4J.svg)](https://jitpack.io/#michaelloo35/Jest4J)

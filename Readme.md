Used this builder because 21 is not supported yet

```dockerfile
FROM gradle:8.3.0-jdk17 AS builder
```

Also have to update build.gradle

```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
```


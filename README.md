GraalVM Native Image Plugin
---

This plugin offers a task (`nativeImage`) to make native executable using GraalVM installed in a machine.

Configuration
---

You can configure options via `nativeImage {}`.

* `graalVmHome` - The Home directory of GraalVM, required.
* `jarTask` - A task of `JarTask` whose output jar will be converted to native executable.(default: `jar` task)
* `mainClass` - A name of main class, required.
* `executableName` - A name of executable, required.
* `runtimeClasspath` - A configuration of runtime classpath.(default: `runtimeClasspath` Configuration)

And you can configure arguments to be passed to GraalVM via `arguments(String...)` method.

Example
---

### script
#### Gradle Groovy DSL
```groovy
plugins {
  id 'java'
  id 'org.mikeneck.graal-native-image' version '0.1.1'
}

repositories {
  mavenCentral()
}

dependencies {
  implementation 'org.slf4j:slf4j-simple:1.7.28'
}

nativeImage {
  graalVmHome = System.getProperty('java.home')
  mainClass = 'com.example.App'
  executableName = 'my-native-application'
  arguments(
      '--no-fallback',
      '--enable-all-security-services',
      '--initialize-at-run-time=com.example.runtime',
      '--report-unsupported-elements-at-runtime'
  )
}
```

#### Gradle Kotlin DSL
```kotlin
plugins {
  kotlin("jvm") version "1.3.50"
  id("org.mikeneck.graalvm-native-image") version "0.1.1"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.slf4j:slf4j-simple:1.7.28")
}

nativeImage {
    setGraalVmHome(System.getProperty("java.home"))
    setMainClass("com.example.App")
    setExecutableName("my-native-application")
    arguments(
        "--no-fallback",
        "--enable-all-security-services",
        "--initialize-at-run-time=com.example.runtime",
        "--report-unsupported-elements-at-runtime"
    )
}
```

### run task

Before running `nativeImage` task, GraalVM and `native-image` command should be installed.

```shell-session
# Prerequisites: GraalVM is installed to your machine.
# Then install native-image.
$ gu install native-image

# Run nativeImage task.
$ ./gradlew nativeImage

# An executable will be created at native-image directory under the project's build directory
$ ls build/native-image
my-native-application
```

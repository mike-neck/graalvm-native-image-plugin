GraalVM Native Image Plugin

![Run Gradle Tests](https://github.com/mike-neck/graalvm-native-image-plugin/workflows/Run%20Gradle%20Tests/badge.svg?branch=master&event=push)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/org/mikeneck/graalvm-native-image/org.mikeneck.graalvm-native-image.gradle.plugin/maven-metadata.xml.svg?colorB=358504&label=gradlePluginPortal)](https://plugins.gradle.org/plugin/org.mikeneck.graalvm-native-image)

---

This plugin offers a task (`nativeImage`), which wraps GraalVM's tool `native-image` installed in a machine.

Configuration
---

### NativeImageTask

You can configure options via `nativeImage {}`.

* `graalVmHome` - The Home directory of GraalVM, required.
* `jarTask` - A task of `JarTask` whose output jar will be converted to native executable.(default: `jar` task)
* `mainClass` - A name of main class, required.
* `executableName` - A name of executable, required.
* `runtimeClasspath` - A configuration of runtime classpath.(default: `runtimeClasspath` Configuration)
* `outputDirectory` - An output directory under which the native image will be generated.(default: `$buildDir/native-image`)

And you can configure arguments to be passed to GraalVM via `arguments(String...)` method.

### GenerateNativeImageConfigTask

You can configure running application parameters by `generateNativeImageConfig {}` block(`GenerateNativeImageConfigTask`).

- `enabled` - A `boolean` property whether to run this task. This plugin disables `generateNativeImageConfig` task in default. Please set `true` to this property to run `generateNativeImageConfig` task.
- `byRunningApplication {}` - Configuration block to run your application. You can configure multiple times.
    - `stdIn(String)` - standard input for your application.
    - `arguments(String...)` - Command line arguments, which will be given to your application.
    - `environment(Map<String, String>)` - Environmental variable for your application.
- `byRunningApplicationWithoutArguments()` - Run application without any configuration.

Example
---

### script
#### Gradle Groovy DSL
```groovy
plugins {
  id 'java'
  id 'org.mikeneck.graalvm-native-image' version 'v0.7.0'
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
  outputDirectory = file("$buildDir/bin")
  arguments(
      '--no-fallback',
      '--enable-all-security-services',
      '--initialize-at-run-time=com.example.runtime',
      '--report-unsupported-elements-at-runtime'
  )
}

generateNativeImageConfig {
  enabled = true
  byRunningApplication {
    stdIn("""
    |total: 2
    |contents:
    |  - name: foo
    |    size: 2052
    |""".stripMargin())
  }
  byRunningApplicationWithoutArguments()
  byRunningApplication {
    arguments('-h')
  }
}
```

#### Gradle Kotlin DSL
```kotlin
import org.mikeneck.graalvm.GenerateNativeImageConfigTask

plugins {
  kotlin("jvm") version "1.3.72"
  id("org.mikeneck.graalvm-native-image") version "v0.7.0"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.slf4j:slf4j-simple:1.7.28")
}

nativeImage {
    graalVmHome = System.getenv("JAVA_HOME")
    mainClass ="com.example.App"
    executableName = "my-native-application"
    outputDirectory = file("$buildDir/executable")
    arguments(
        "--no-fallback",
        "--enable-all-security-services",
        "--initialize-at-run-time=com.example.runtime",
        "--report-unsupported-elements-at-runtime"
    )
}

generateNativeImageConfig {
  enabled = true
  byRunningApplication {
    stdIn("""
      |total: 2
      |contents:
      |  - name: foo
      |    size: 2052
      |""".trimMargin())
  }
  byRunningApplicationWithoutArguments()
  byRunningApplication {
    arguments('-h')
  }
}
```

### run task

##### For linux/mac users

(Optional)Before running `nativeImage` task, GraalVM and `native-image` command should be installed.
Version v0.5.0 or later, the plugin has `installNativeImage` task which execute installation command(`gu install native-image`)
so that users do not need to run `gu` command.

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

##### For Windows users

Make sure you are running `nativeImage` task on Windows SDK 7.1 Command Prompt.

##### For GitHub Actions

If you are planning releasing both MacOS X and Linux applications, please refer example workflow under `example` directory.

### generateNativeImageConfig task

This task requires `native-image` command. If your machine has no `native-image` command,
run `gu` command or `installNativeImage` task before running `generateNativeImageConfig` task.

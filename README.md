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
* `mainClass` - (Deprecated, configure via `buildType { executable { main = ... } }`) A name of main class, required.
* `executableName` - A name of executable, required.
* `runtimeClasspath` - A configuration of runtime classpath.(default: `runtimeClasspath` Configuration)
* `outputDirectory` - An output directory under which the native image will be generated.(default: `$buildDir/native-image`)
* `buildType {}` - A type of build, required. `BuildTypeSelector` will be passed to this closure.
    - `buildTypeSelector.sharedLibrary` - To build shared library, return this.
    - `buildTypeSelector.executable {}` - To build executable, call `executable {}` block, and configure its `main` with the application's main class name.

You can configure arguments to be passed to GraalVM via `arguments(String...)` method.

- `arguments(String...)` - `native-image` command's arguments.
- `arguments(Provider<String>...)` - `native-image` command's arguments with `Provider<String>` type.
- `arguments {}` - Configure `native-image` command's arguments in configuration block.
  - `add(String)` - Equivalent to `arguments(String...)`.
  - `add(Provider<String>)` - Equivalent to `arguments(Provider<String>...)`.

For more information, please see appendix at the bottom of this README.

### GenerateNativeImageConfigTask

You can configure running application parameters by `generateNativeImageConfig {}` block(`GenerateNativeImageConfigTask`).

- `enabled` - A `boolean` property whether to run this task. This plugin disables `generateNativeImageConfig` task in default. Please set `true` to this property to run `generateNativeImageConfig` task.
- `mainClass` - The main class name for running application.
- `byRunningApplication {}` - Configuration block to run your application. You can configure multiple times.
    - `stdIn(String)` - standard input for your application.
    - `arguments(String...)` - Command line arguments, which will be given to your application.
    - `environment(Map<String, String>)` - Environmental variable for your application.
- `byRunningApplicationWithoutArguments()` - Run application without any configuration.

Example
---

### 1. Example script of building executable.
#### Gradle Groovy DSL
```groovy
plugins {
  id 'java'
  id 'org.mikeneck.graalvm-native-image' version 'v1.4.0'
}

repositories {
  mavenCentral()
}

dependencies {
  implementation 'org.slf4j:slf4j-simple:1.7.28'
}

import org.mikeneck.graalvm.BuildTypeSelector

nativeImage {
  graalVmHome = System.getProperty('java.home')
  mainClass = 'com.example.App' // Deprecated, use buildType.executable.main as follows instead.
  buildType { BuildTypeSelector build ->
    build.executable {
      main = 'com.example.App'
    }
  }
  executableName = 'my-native-application'
  outputDirectory = file("$buildDir/bin")
  arguments {
    add '--no-fallback'
    add '--enable-all-security-services'
    add options.traceClassInitialization('com.example.MyDataProvider,com.example.MyDataConsumer')
    add '--initialize-at-run-time=com.example.runtime'
    add '--report-unsupported-elements-at-runtime'
  }
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
  id("org.mikeneck.graalvm-native-image") version "v1.4.0"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.slf4j:slf4j-simple:1.7.28")
}

nativeImage {
    graalVmHome = System.getenv("JAVA_HOME")
    mainClass ="com.example.App" // Deprecated, use `buildType.executable.main` as follows instead.
    buildType { build ->
      build.executable(main = 'com.example.App')
    }
    executableName = "my-native-application"
    outputDirectory = file("$buildDir/executable")
    arguments(
        "--no-fallback",
        "--enable-all-security-services",
        options.traceClassInitialization('com.example.MyDataProvider,com.example.MyDataConsumer'),
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

### 2. Example script of building shared library.

Shared library feature is one of GraalVM's feature to build shared library('so' file in linux, 'dylib' file in OS X), only available Java11 or later.

#### Gradle Groovy DSL
```groovy
plugins {
  id 'java'
  id 'org.mikeneck.graalvm-native-image' version 'v1.4.0'
}

repositories {
  mavenCentral()
}

dependencies {
  implementation 'org.slf4j:slf4j-simple:1.7.28'
}

import org.mikeneck.graalvm.BuildTypeSelector

nativeImage {
  graalVmHome = System.getProperty('java.home')
  buildType { BuildTypeSelector build ->
    build.sharedLibrary
  }
  executableName = 'my-native-lib'
  outputDirectory = file("$buildDir/native-lib")
  arguments {
    add '--no-fallback'
    add '--enable-all-security-services'
    add options.traceClassInitialization('com.example.MyDataProvider,com.example.MyDataConsumer')
    add '--initialize-at-run-time=com.example.runtime'
    add '--report-unsupported-elements-at-runtime'
  }
}

generateNativeImageConfig {
  enabled = true
  // If config file generation is required, set main class name here in generateNativeImageConfig block.
  mainClass = 'com.example.RunMainForGenerateConfigJson'
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
  id("org.mikeneck.graalvm-native-image") version "v1.4.0"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.slf4j:slf4j-simple:1.7.28")
}

nativeImage {
    graalVmHome = System.getenv("JAVA_HOME")
    buildType { sharedLibrary }
    executableName = "my-native-lib"
    outputDirectory = file("$buildDir/native-lib")
    arguments(
        "--no-fallback",
        "--enable-all-security-services",
        options.traceClassInitialization('com.example.MyDataProvider,com.example.MyDataConsumer'),
        "--initialize-at-run-time=com.example.runtime",
        "--report-unsupported-elements-at-runtime"
    )
}

generateNativeImageConfig {
  enabled = true
  // If config file generation is required, set main class name here in generateNativeImageConfig block.
  mainClass = "com.example.RunMainForGenerateConfigJson"
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

---

`nativeImage` task configuration appendix
---

### `TraceClassInitialization` option

As of GraalVM 20.3.0, the way to pass `TraceClassInitialization` option is changed.
So we offer a convenient way to create `TraceClassInitialization` option.
In this way you can create the option via an `options` object available from `nativeImageTask`.

```groovy
nativeImage {
  arguments {
    // When used with GraalVM 20.3.0 or later
    // -H:TraceClassInitialization=com.example.WantTracingInInitializationClass,com.example.Another
    // When used with GraalVM 20.2.0 or earlier
    // -H:+TraceClassInitialization
    add options.traceClassInitialization { 'com.example.WantTracingInInitializationClass,com.example.Another' }
  }
}
```

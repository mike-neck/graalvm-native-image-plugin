package org.mikeneck.graalvm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.TaskOutcome;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestProjectSetup.class)
class Case145SharedLibrary {

  @DisabledForJreRange(max = JRE.JAVA_8)
  @Test
  @TestProject(value = "case-145-shared-library")
  void run(@NotNull Gradlew gradlew, @NotNull FunctionalTestContext context) {
    BuildResult result = gradlew.invoke("nativeImage");

    Path headerFile = context.file("build/native-image/hash-lib.h");
    Path soFile = context.file("build/native-image/hash-lib.so");
    Path dylibFile = context.file("build/native-image/hash-lib.dylib");
    assertAll(
        () ->
            assertThat(result.tasks(TaskOutcome.SUCCESS).stream().map(BuildTask::getPath))
                .contains(":nativeImage"),
        () -> assertThat(headerFile).exists(),
        () ->
            assertThat(Files.readAllLines(headerFile))
                .contains(
                    "int kotlin_lib_com_example_add(long long int, int, int);",
                    "int java_lib_com_example_get_hash(long long int, int, char*);"),
        () ->
            assertThat(Arrays.asList(soFile, dylibFile))
                .anySatisfy(lib -> assertThat(lib).exists()));
  }

  @Test
  @TestProject("case-145-main-class-name")
  void mainClassName(@NotNull Gradlew gradlew, @NotNull FunctionalTestContext context) {
    BuildResult result = gradlew.invoke("nativeImage", "--stacktrace");
    Path executable = context.file("build/native-image/hash-lib");
    assertAll(
        () ->
            assertThat(result.tasks(TaskOutcome.SUCCESS).stream().map(BuildTask::getPath))
                .contains(":nativeImage"),
        () -> assertThat(executable).exists());
  }
}

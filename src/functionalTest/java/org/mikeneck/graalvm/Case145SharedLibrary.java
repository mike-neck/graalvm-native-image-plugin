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
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestProjectSetup.class)
class Case145SharedLibrary {

  @Test
  @TestProject(value = "case-145-shared-library")
  void run(@NotNull Gradlew gradlew, @NotNull FunctionalTestContext context) {
    BuildResult result = gradlew.invoke("nativeImage");

    Path headerFile = context.file("build/shared-lib/shared-lib.h");
    Path soFile = context.file("build/shared-lib/shared-lib.so");
    Path dylibFile = context.file("build/shared-lib/shared-lib.dylib");
    assertAll(
        () ->
            assertThat(result.tasks(TaskOutcome.SUCCESS).stream().map(BuildTask::getPath))
                .contains(":nativeImage"),
        () -> assertThat(headerFile).exists(),
        () -> assertThat(Files.readAllLines(headerFile)).contains("kotlin_lib_com_example_add", "java_lib_com_example_get_hash"),
        () ->
            assertThat(Arrays.asList(soFile, dylibFile))
                .anySatisfy(lib -> assertThat(lib).exists()));
  }
}

package org.mikeneck.graalvm;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.TaskOutcome;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

class Case93WithoutJarTaskWithDifferentConfiguration {

  @Test
  @ExtendWith(TestProjectSetup.class)
  @TestProject(value = "case93-without-jar-with-different-configuration", subprojects = "child")
  void runCase(@NotNull final Gradlew gradlew) {
    BuildResult result = gradlew.invoke("clean", "--stacktrace", "nativeImage");
    assertThat(result.task(":nativeImage"))
        .satisfies(buildTask -> assertThat(buildTask.getOutcome()).isEqualTo(TaskOutcome.SUCCESS));
  }

  @Test
  @ExtendWith(TestProjectSetup.class)
  @TestProject(
      value = "case93-subproject-app",
      subprojects = {"sub", "lib"})
  void anotherCase(@NotNull final Gradlew gradlew) {
    BuildResult result = gradlew.invoke("clean", "--stacktrace", "nativeImage");
    assertThat(result.task(":nativeImage"))
        .satisfies(buildTask -> assertThat(buildTask.getOutcome()).isEqualTo(TaskOutcome.SUCCESS));
  }
}

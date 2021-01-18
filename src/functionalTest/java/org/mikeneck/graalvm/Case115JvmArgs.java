package org.mikeneck.graalvm;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.TaskOutcome;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

class Case115JvmArgs {

  @Test
  @ExtendWith(TestProjectSetup.class)
  @TestProject(value = "case-115-jvm-args", subprojects = "foo")
  void run(@NotNull Gradlew gradlew) {
    BuildResult result = gradlew.invoke("nativeImage");
    result.tasks(TaskOutcome.SUCCESS).forEach(System.out::println);
  }
}

package org.mikeneck.graalvm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.TaskOutcome;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestProjectSetup.class)
class Case143UsingFileTest {

  @Test
  @TestProject("case-143-using-file")
  void preferByFile(@NotNull Gradlew gradlew, @NotNull FunctionalTestContext context) {
    BuildResult result = gradlew.invoke("nativeImage", "--info");

    assertAll(
        () ->
            assertThat(result.tasks(TaskOutcome.SUCCESS))
                .anyMatch(task -> task.getPath().equals("nativeImage")),
        () -> assertThat(context.file("build/tmp/native-image-args/arguments.txt")).exists());
  }
}

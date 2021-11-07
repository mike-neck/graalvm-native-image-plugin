package org.mikeneck.graalvm;

import static org.assertj.core.api.Assertions.assertThat;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.TaskOutcome;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @see <a href="https://github.com/mike-neck/graalvm-native-image-plugin/issues/175">GitHub
 *     Issue</a>
 */
class Case175WithoutMainClass {

  @Test
  @ExtendWith(TestProjectSetup.class)
  @TestProject(value = "case-175-without-mainclass")
  void run(@NotNull Gradlew gradlew, @NotNull FunctionalTestContext context) {
    BuildResult result = gradlew.invoke("nativeImage", "--info");
    assertThat(result.tasks(TaskOutcome.SUCCESS).stream().map(BuildTask::getPath))
        .contains(":nativeImage");
  }
}

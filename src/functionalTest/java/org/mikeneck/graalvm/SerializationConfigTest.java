package org.mikeneck.graalvm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.TaskOutcome;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

public class SerializationConfigTest {

  @Test
  @ExtendWith(TestProjectSetup.class)
  @TestProject(value = "serialization-config")
  void outputConfig(@NotNull final Gradlew gradlew, @NotNull FunctionalTestContext context) {
    BuildResult result = gradlew.invoke("clean", "generateNativeImageConfig");
    assertAll(
        () ->
            assertThat(result.task(":generateNativeImageConfig"))
                .satisfies(
                    buildTask -> assertThat(buildTask.getOutcome()).isEqualTo(TaskOutcome.SUCCESS)),
        () ->
            assertThat(
                    context.rootDir.resolve(
                        "build/tmp/native-image-config/out-0/serialization-config.json"))
                .exists(),
        () ->
            assertThat(
                    context.rootDir.resolve("build/native-image-config/serialization-config.json"))
                .exists());
  }
}

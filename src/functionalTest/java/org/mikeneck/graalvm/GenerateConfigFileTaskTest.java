package org.mikeneck.graalvm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateConfigFileTaskTest {

  @SuppressWarnings("DuplicatedCode")
  @Test
  void generateAndMergeNativeImageConfig() {
    FunctionalTestContext context = new FunctionalTestContext("config-project");
    context.setup();
    Path projectDir = context.rootDir;

    GradleRunner runner = GradleRunner.create();
    runner.forwardOutput();
    runner.withPluginClasspath();
    runner.withArguments(
        "clean", "generateNativeImageConfig", "--stacktrace", "--info", "--warning-mode", "all");
    runner.withProjectDir(projectDir.toFile());
    BuildResult result = runner.build();

    List<String> succeededTasks =
        result.tasks(TaskOutcome.SUCCESS).stream()
            .map(BuildTask::getPath)
            .collect(Collectors.toList());
    System.out.println(succeededTasks);
    assertThat(succeededTasks)
        .contains(
            ":clean",
            ":compileJava",
            ":processResources",
            ":classes",
            ":jar",
            ":generateNativeImageConfig",
            ":mergeNativeImageConfig");
    assertTrue(Files.exists(projectDir.resolve("build/tmp/native-image-config/out-0")));
    assertTrue(Files.exists(projectDir.resolve("build/tmp/native-image-config/out-1")));
    assertTrue(Files.exists(projectDir.resolve("build/tmp/native-image-config/out-2")));
    assertTrue(Files.exists(projectDir.resolve("build/tmp/native-image-config/out-3")));
    assertTrue(Files.exists(projectDir.resolve("build/tmp/native-image-config/out-4")));
    assertTrue(Files.exists(projectDir.resolve("build/tmp/native-image-config/out-5")));
    assertTrue(Files.exists(projectDir.resolve("build/tmp/native-image-config/out-6")));
    assertTrue(Files.exists(projectDir.resolve("build/tmp/native-image-config/out-7")));
    assertTrue(Files.exists(projectDir.resolve("build/native-image-config/jni-config.json")));
    assertTrue(Files.exists(projectDir.resolve("build/native-image-config/proxy-config.json")));
    assertTrue(Files.exists(projectDir.resolve("build/native-image-config/reflect-config.json")));
    assertTrue(Files.exists(projectDir.resolve("build/native-image-config/resource-config.json")));
  }

  @SuppressWarnings("DuplicatedCode")
  @Test
  void buildNativeImageWithConfiguration() {
    FunctionalTestContext context = new FunctionalTestContext("native-image-with-config");
    context.setup();
    Path projectDir = context.rootDir;

    GradleRunner runner = GradleRunner.create();
    runner.forwardOutput();
    runner.withPluginClasspath();
    runner.withArguments("clean", "nativeImage", "--stacktrace", "--info", "--warning-mode", "all");
    runner.withProjectDir(projectDir.toFile());
    BuildResult result = runner.build();

    assertTrue(Files.exists(projectDir.resolve("build/native-image/test-app")));
    TaskOutcome nativeImageResult =
        Objects.requireNonNull(result.task(":nativeImage")).getOutcome();
    assertThat(nativeImageResult).isEqualTo(TaskOutcome.SUCCESS);
  }

  @SuppressWarnings("DuplicatedCode")
  @Test
  void buildNativeImageWithConfigurationOnKotlinProject() {
    FunctionalTestContext context = new FunctionalTestContext("config-kotlin-project");
    context.setup();
    Path projectDir = context.rootDir;

    GradleRunner runner = GradleRunner.create();
    runner.forwardOutput();
    runner.withPluginClasspath();
    runner.withArguments("clean", "nativeImage", "--stacktrace", "--info", "--warning-mode", "all");
    runner.withProjectDir(projectDir.toFile());
    BuildResult result = runner.build();

    assertTrue(Files.exists(projectDir.resolve("build/image/json2yaml")));
    TaskOutcome nativeImageResult =
        Objects.requireNonNull(result.task(":nativeImage")).getOutcome();
    assertThat(nativeImageResult).isEqualTo(TaskOutcome.SUCCESS);
    assertTrue(Files.exists(projectDir.resolve("build/tmp/native-image-config/out-2")));
  }

  @SuppressWarnings("DuplicatedCode")
  @Test
  void dryRunNativeImageConfig() {
    FunctionalTestContext context =
        new FunctionalTestContext("config-project", "config-project-dry-run");
    context.setup();
    Path projectDir = context.rootDir;

    GradleRunner runner = GradleRunner.create();
    runner.forwardOutput();
    runner.withPluginClasspath();
    runner.withArguments("generateNativeImageConfig", "--dry-run");
    runner.withProjectDir(projectDir.toFile());
    BuildResult result = runner.build();

    String succeededTasks = result.getOutput();

    assertThat(succeededTasks)
        .contains(
            ":installNativeImage SKIPPED",
            ":compileJava SKIPPED",
            ":processResources SKIPPED",
            ":classes SKIPPED",
            ":jar SKIPPED",
            ":generateNativeImageConfig SKIPPED",
            ":mergeNativeImageConfig SKIPPED");
  }

  @SuppressWarnings("DuplicatedCode")
  @Test
  void nativeImageNativeImageConfig() {
    FunctionalTestContext context =
        new FunctionalTestContext("config-project", "config-project-native-image");
    context.setup();
    Path projectDir = context.rootDir;

    GradleRunner runner = GradleRunner.create();
    runner.forwardOutput();
    runner.withPluginClasspath();
    runner.withArguments("nativeImage");
    runner.withProjectDir(projectDir.toFile());
    BuildResult result = runner.build();

    Path nativeBinary = projectDir.resolve("build/native-image/test-app");
    assertThat(nativeBinary).exists().isExecutable();
    List<String> tasks =
        result.tasks(TaskOutcome.SUCCESS).stream()
            .map(BuildTask::getPath)
            .collect(Collectors.toList());
    assertThat(tasks).contains(":nativeImage");
  }
}

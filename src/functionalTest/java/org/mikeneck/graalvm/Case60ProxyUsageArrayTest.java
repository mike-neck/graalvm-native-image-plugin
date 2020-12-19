package org.mikeneck.graalvm;

import java.util.List;
import java.util.stream.Collectors;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class Case60ProxyUsageArrayTest {

    @Test
    void runCase() {
        FunctionalTestContext context = new FunctionalTestContext("case60-proxy-usage-array");
        context.setup();

        GradleRunner runner = GradleRunner.create();
        BuildResult result = runner
                .withProjectDir(context.rootDir.toFile())
                .forwardOutput()
                .withPluginClasspath()
                .withArguments("generateNativeImageConfig", "--warning-mode", "all")
                .build();

        assertAll(
                () -> {
                    List<String> tasks = result.tasks(TaskOutcome.SUCCESS)
                            .stream().map(BuildTask::getPath)
                            .collect(Collectors.toList());
                    assertThat(tasks).contains(
                            ":compileJava",
                            ":classes",
                            ":jar",
                            ":generateNativeImageConfig",
                            ":mergeNativeImageConfig");
                },
                () -> assertThat(
                        context.file("build/tmp/native-image-config/out-0"))
                        .exists(),
                () -> assertThat(
                        context.file("build/tmp/native-image-config/out-1"))
                        .exists(),
                () -> assertThat(
                        context.file("build/tmp/native-image-config/out-2"))
                        .exists(),
                () -> assertThat(
                        context.fileTextContentsOf("build/native-image-config/proxy-config.json"))
                        .isEqualTo("[[\"com.example.ExitCode\",\"com.example.Printer\"]]")
        );
    }

    @Test
    void createNativeImage() {
        FunctionalTestContext context = new FunctionalTestContext("case60-proxy-usage-array", "case60-native-image");
        context.setup();

        GradleRunner runner = GradleRunner.create();
        runner
                .withProjectDir(context.rootDir.toFile())
                .forwardOutput()
                .withPluginClasspath()
                .withArguments("nativeImage", "--warning-mode", "all")
                .build();

        assertThat(context.file("build/image/pst8pdt-time"))
                .exists()
                .isRegularFile()
                .isExecutable();
    }
}

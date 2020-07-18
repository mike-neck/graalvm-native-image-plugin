/*
 * Copyright 2020 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mikeneck.graalvm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class GenerateConfigFileTaskTest {

    @Test
    public void generateAndMergeNativeImageConfig() {
        FunctionalTestContext context = new FunctionalTestContext("config-project");
        context.setup();
        Path projectDir = context.rootDir;

        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("clean","generateNativeImageConfig", "--stacktrace", "--info", "--warning-mode", "all");
        runner.withProjectDir(projectDir.toFile());
        BuildResult result = runner.build();

        List<String> succeededTasks = result.tasks(TaskOutcome.SUCCESS).stream()
                .map(BuildTask::getPath)
                .collect(Collectors.toList());
        System.out.println(succeededTasks);
        assertThat(succeededTasks, is(Arrays.asList(
                ":clean",
                ":compileJava",
                ":processResources",
                ":classes",
                ":jar",
                ":generateNativeImageConfig",
                ":mergeNativeImageConfig")));
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

    @Test
    public void buildNativeImageWithConfiguration() {
        FunctionalTestContext context = new FunctionalTestContext("native-image-with-config");
        context.setup();
        Path projectDir = context.rootDir;

        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("clean","nativeImage", "--stacktrace", "--info", "--warning-mode", "all");
        runner.withProjectDir(projectDir.toFile());
        BuildResult result = runner.build();

        assertTrue(Files.exists(projectDir.resolve("build/native-image/test-app")));
        TaskOutcome nativeImageResult = result.task(":nativeImage").getOutcome();
        assertThat(nativeImageResult, is(TaskOutcome.SUCCESS));
    }

    @Test
    public void buildNativeImageWithConfigurationOnKotlinProject() {
        FunctionalTestContext context = new FunctionalTestContext("config-kotlin-project");
        context.setup();
        Path projectDir = context.rootDir;

        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("clean","nativeImage", "--stacktrace", "--info", "--warning-mode", "all");
        runner.withProjectDir(projectDir.toFile());
        BuildResult result = runner.build();

        assertTrue(Files.exists(projectDir.resolve("build/image/json2yaml")));
        TaskOutcome nativeImageResult = result.task(":nativeImage").getOutcome();
        assertThat(nativeImageResult, is(TaskOutcome.SUCCESS));
        assertTrue(Files.exists(projectDir.resolve("build/tmp/native-image-config/out-2")));
    }
}

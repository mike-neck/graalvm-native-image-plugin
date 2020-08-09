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


}

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

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.TaskOutcome;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

class Case93WithoutJarTaskWithDifferentConfiguration {

    @Test
    @ExtendWith(TestProjectSetup.class)
    @TestProject(
            value = "case93-without-jar-with-different-configuration",
            subprojects = "child")
    void runCase(@NotNull final Gradlew gradlew) {
        BuildResult result = gradlew.invoke("clean", "--stacktrace", "nativeImage");
        assertThat(result.task(":nativeImage"))
                .satisfies(buildTask -> 
                        assertThat(buildTask.getOutcome()).isEqualTo(TaskOutcome.SUCCESS));
    }

    @Test
    @ExtendWith(TestProjectSetup.class)
    @TestProject(
            value = "case93-subproject-app",
            subprojects = {"sub", "lib"})
    void anotherCase(@NotNull final Gradlew gradlew) {
        BuildResult result = gradlew.invoke("clean", "--stacktrace", "nativeImage");
        assertThat(result.task(":nativeImage"))
                .satisfies(buildTask ->
                        assertThat(buildTask.getOutcome()).isEqualTo(TaskOutcome.SUCCESS));
    }
}

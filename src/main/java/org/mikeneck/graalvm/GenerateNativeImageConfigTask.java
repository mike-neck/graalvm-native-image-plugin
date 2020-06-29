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

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.jetbrains.annotations.NotNull;

public interface GenerateNativeImageConfigTask extends ShareEnabledState {

    default void setGraalVmHome(@NotNull String path) {
        setGraalVmHome(getProject().file(path));
    }

    default void setGraalVmHome(@NotNull File path) {
        setGraalVmHome(path.toPath());
    }

    default void setGraalVmHome(@NotNull Path path) {
        Project project = getProject();
        setGraalVmHome(project.provider(() -> new GraalVmHome(path)));
    }

    void setGraalVmHome(@NotNull Provider<GraalVmHome> graalVmHome);

    void setExitOnApplicationError(boolean exitOnApplicationError);

    @Internal
    @NotNull Property<GraalVmHome> getGraalVmHome();

    @Internal
    boolean getExitOnApplicationError();

    @Input
    @NotNull Provider<String> getMainClass();

    void resumeOnApplicationError();

    @NotNull
    @Nested
    List<JavaExecutionImpl> getJavaExecutions();

    @Deprecated
    @OutputDirectory
    @NotNull File getTemporaryDirectory();

    void byRunningApplicationWithoutArguments();

    void byRunningApplication(Action<JavaExecution> argumentsConfiguration);
}

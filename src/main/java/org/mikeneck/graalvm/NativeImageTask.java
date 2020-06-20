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
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.Directory;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.nativeimage.NativeImageArguments;

public interface NativeImageTask extends Task, NativeImageConfig {

    @NotNull
    @Nested
    NativeImageArguments getNativeImageArguments();

    @Override
    void setGraalVmHome(String graalVmHome);

    @Override
    void setJarTask(Jar jarTask);

    @Override
    void setMainClass(String mainClass);

    @Override
    void setExecutableName(String name);

    @Override
    void setRuntimeClasspath(Configuration configuration);

    @Override
    default void setOutputDirectory(File directory) {
        Project project = getProject();
        ProjectLayout projectLayout = project.getLayout();
        Provider<Directory> dir = projectLayout.dir(project.provider(() -> directory));
        setOutputDirectory(dir);
    }

    @Override
    default void setOutputDirectory(Path directory) {
        setOutputDirectory(directory.toFile());
    }

    @Override
    default void setOutputDirectory(String directory) {
        File dir = getProject().file(directory);
        setOutputDirectory(dir);
    }

    @Override
    void setOutputDirectory(Provider<Directory> directory);

    void withConfigFiles(@NotNull Action<NativeImageConfigurationFiles> configuration);

    @Override
    void arguments(String... arguments);
}

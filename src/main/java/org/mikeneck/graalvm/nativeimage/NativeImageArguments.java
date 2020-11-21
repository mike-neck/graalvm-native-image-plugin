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
package org.mikeneck.graalvm.nativeimage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.NativeImageConfigurationFiles;

public interface NativeImageArguments {

    @Internal
    default List<String> getArguments() {
        List<String> args = new ArrayList<>();
        args.add("-cp");
        args.add(classpath());
        args.add(outputPath());
        executableName().ifPresent(args::add);
        args.addAll(additionalArguments());
        args.add(mainClass());
        return Collections.unmodifiableList(args);
    }

    @NotNull
    String classpath();

    @NotNull
    String outputPath();

    @NotNull
    Optional<String> executableName();

    @NotNull
    List<String> additionalArguments();

    @NotNull
    String mainClass();

    void setRuntimeClasspath(@NotNull Provider<Configuration> runtimeClasspath);

    void setMainClass(@NotNull Provider<String> mainClass);

    void addClasspath(@NotNull File jarFile);

    void addClasspath(@NotNull Provider<File> jarFile);

    void addClasspath(@NotNull FileCollection files);

    void addClasspath(@NotNull Jar jar);

    void setClasspath(@NotNull File jarFile);

    void setClasspath(@NotNull Provider<File> jarFile);

    void setClasspath(@NotNull FileCollection files);

    void setClasspath(@NotNull Jar jar);

    @NotNull
    @OutputDirectory
    DirectoryProperty getOutputDirectory();

    void setOutputDirectory(@NotNull Provider<Directory> outputDirectory);

    void setExecutableName(@NotNull Provider<String> executableName);

    void addArguments(@NotNull Provider<Iterable<String>> arguments);

    void configureConfigFiles(@NotNull Action<NativeImageConfigurationFiles> configuration);
}

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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.jetbrains.annotations.NotNull;

class UnixLikeOsArguments implements NativeImageArguments {

    @NotNull
    private final Provider<Configuration> runtimeClasspath;
    @NotNull
    private final Provider<String> mainClass;
    @NotNull
    private final Provider<File> jarFile;
    @NotNull
    private final Provider<File> outputDirectory;
    @NotNull
    private final Provider<String> executableName;
    @NotNull
    private final ListProperty<String> additionalArguments;

    UnixLikeOsArguments(
            @NotNull Provider<Configuration> runtimeClasspath,
            @NotNull Provider<String> mainClass,
            @NotNull Provider<File> jarFile,
            @NotNull Provider<File> outputDirectory,
            @NotNull Provider<String> executableName,
            @NotNull ListProperty<String> additionalArguments) {
        this.runtimeClasspath = runtimeClasspath;
        this.mainClass = mainClass;
        this.jarFile = jarFile;
        this.outputDirectory = outputDirectory;
        this.executableName = executableName;
        this.additionalArguments = additionalArguments;
    }

    @NotNull
    @Override
    public String classpath() {
        List<File> paths = new ArrayList<>(runtimeClasspath());
        paths.add(jarFile.get());
        return paths.stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(File.pathSeparator));
    }

    @NotNull
    private Collection<File> runtimeClasspath() {
        return runtimeClasspath.map(Configuration::getFiles)
                .get();
    }

    @NotNull
    @Override
    public String outputPath() {
        return String.format("-H:Path=%s", outputDirectory.map(File::getAbsolutePath).get());
    }

    @NotNull
    @Override
    public Optional<String> executableName() {
        if (executableName.isPresent()) {
            return Optional.of(String.format("-H:Name=%s", executableName.get()));
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public List<String> additionalArguments() {
        return additionalArguments.isPresent()? additionalArguments.get(): Collections.emptyList();
    }

    @Override
    public @NotNull String mainClass() {
        return mainClass.get();
    }

    @NotNull
    @InputFiles
    public Provider<Configuration> getRuntimeClasspath() {
        return runtimeClasspath;
    }

    @NotNull
    @Input
    public Provider<String> getMainClass() {
        return mainClass;
    }

    @NotNull
    @InputFile
    public Provider<File> getJarFile() {
        return jarFile;
    }

    @NotNull
    @OutputDirectory
    public Provider<File> getOutputDirectory() {
        return outputDirectory;
    }

    @NotNull
    @Input
    public Provider<String> getExecutableName() {
        return executableName;
    }

    @NotNull
    @Input
    public ListProperty<String> getAdditionalArguments() {
        return additionalArguments;
    }
}

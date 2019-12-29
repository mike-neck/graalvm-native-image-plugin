/*
 * Copyright 2019 Shinya Mochida
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

import org.gradle.api.Project;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public interface NativeImageArguments {

    String classpath();

    String outputPath();

    Optional<String> executableName();

    List<String> additionalArguments();

    String mainClass();

    static NativeImageArguments create(Project project, NativeImageExtension extension) {
        NixLikeOsArguments arguments = new NixLikeOsArguments(project, extension);
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            project.getLogger().info("use WindowsArguments because os.name is {}", osName);
            return new WindowsArguments(arguments);
        }
        project.getLogger().info("use NixLikeArguments because os.name is {}", osName);
        return arguments;
    }
}

class NixLikeOsArguments implements NativeImageArguments {
    private final Project project;
    private final NativeImageExtension extension;

    NixLikeOsArguments(Project project, NativeImageExtension extension) {
        this.project = project;
        this.extension = extension;
    }

    @Override
    public String classpath() {
        List<File> paths = new ArrayList<>(runtimeClasspath());
        paths.add(jarFile());
        return paths
                .stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(File.pathSeparator));
    }

    private Collection<File> runtimeClasspath() {
        return extension.runtimeClasspath.get().getFiles();
    }

    private File jarFile() {
        return extension.jarTask.get().getOutputs().getFiles().getSingleFile();
    }

    @Override
    public String outputPath() {
        return "-H:Path=" + outputDirectory().toAbsolutePath().toString();
    }

    private Path outputDirectory() {
        return project.getBuildDir().toPath().resolve("native-image");
    }

    @Override
    public Optional<String> executableName() {
        if (extension.executableName.isPresent()) {
            return Optional.of("-H:Name=" + extension.executableName.get());
        }
        return Optional.empty();
    }

    @Override
    public List<String> additionalArguments() {
        if (extension.additionalArguments.isPresent()) {
            return extension.additionalArguments.get();
        }
        return Collections.emptyList();
    }

    @Override
    public String mainClass() {
        return extension.mainClass.get();
    }
}

class WindowsArguments implements NativeImageArguments {

    private static final char DOUBLE_QUOT = '"';

    private final NativeImageArguments delegate;

    WindowsArguments(NativeImageArguments delegate) {
        this.delegate = delegate;
    }

    @Override
    public String classpath() {
        return DOUBLE_QUOT + delegate.classpath() + DOUBLE_QUOT;
    }

    @Override
    public String outputPath() {
        return DOUBLE_QUOT + delegate.outputPath() + DOUBLE_QUOT;
    }

    @Override
    public Optional<String> executableName() {
        return delegate.executableName().map(name -> DOUBLE_QUOT + name + DOUBLE_QUOT);
    }

    @Override
    public List<String> additionalArguments() {
        return delegate
                .additionalArguments()
                .stream()
                .map(argument -> DOUBLE_QUOT + argument + DOUBLE_QUOT)
                .collect(Collectors.toList());
    }

    @Override
    public String mainClass() {
        return DOUBLE_QUOT + delegate.mainClass() + DOUBLE_QUOT;
    }
}

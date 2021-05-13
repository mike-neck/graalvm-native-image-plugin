package org.mikeneck.graalvm.nativeimage;

import java.io.File;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.jetbrains.annotations.NotNull;

public interface NativeImageState {

    @NotNull
    @InputFiles
    Provider<Configuration> getRuntimeClasspath();

    @NotNull
    @Internal
    Provider<BuildTypeOption> getBuildType();

    @InputFiles
    @NotNull Iterable<File> getJarFiles();

    @NotNull
    @OutputDirectory
    DirectoryProperty getOutputDirectory();

    @NotNull
    @Input
    Provider<String> getExecutableName();

    @NotNull
    @Input
    ListProperty<String> getAdditionalArguments();
}

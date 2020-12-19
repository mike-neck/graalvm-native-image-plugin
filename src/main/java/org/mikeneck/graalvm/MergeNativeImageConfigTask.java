package org.mikeneck.graalvm;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.config.task.ConfigFileConfiguration;

public interface MergeNativeImageConfigTask extends Task {

    Function<Project, Provider<Directory>> DEFAULT_DESTINATION_DIR =
            project -> project.getLayout().getBuildDirectory().dir("native-image-config");

    String JNI_CONFIG_JSON = "jni-config.json";
    String PROXY_CONFIG_JSON = "proxy-config.json";
    String REFLECT_CONFIG_JSON = "reflect-config.json";
    String RESOURCE_CONFIG_JSON = "resource-config.json";

    void destinationDir(@NotNull String destinationDir);

    void destinationDir(@NotNull File destinationDir);

    void destinationDir(@NotNull Path destinationDir);

    void destinationDir(@NotNull Provider<Directory> destinationDir);

    void fromDirectories(@NotNull Provider<FileCollection> directories);

    void configFiles(@NotNull Action<ConfigFileConfiguration> action);

    @NotNull 
    @OutputDirectory
    DirectoryProperty getDestinationDir();

    @NotNull
    @InputFiles
    ListProperty<File> getJniConfigs();

    @NotNull
    @InputFiles
    ListProperty<File> getProxyConfigs();

    @NotNull
    @InputFiles
    ListProperty<File> getReflectConfigs();

    @NotNull
    @InputFiles
    ListProperty<File> getResourceConfigs();
}

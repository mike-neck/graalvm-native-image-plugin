package org.mikeneck.graalvm;

import java.io.File;
import java.nio.file.Path;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.nativeimage.NativeImageArguments;
import org.mikeneck.graalvm.nativeimage.options.Options;

public interface NativeImageTask extends Task, NativeImageConfig {

    @NotNull
    @Internal
    Provider<GraalVmVersion> getGraalVmVersion();

    @NotNull
    @Internal
    Options getOptions();

    @NotNull
    @Nested
    NativeImageArguments getNativeImageArguments();

    @Override
    void setGraalVmHome(String graalVmHome);

    /**
     * @param jarTask - jarTask which builds application.
     * @deprecated use {@link #setClasspath} instead.
     */
    @Deprecated
    @Override
    void setJarTask(Jar jarTask);

    @Override
    void setClasspath(FileCollection files);

    default void setClasspath(File file) {
        ConfigurableFileCollection files = getProject().files(file);
        this.setClasspath(files);
    }

    default void setClasspath(Provider<File> file) {
        ConfigurableFileCollection files = getProject().files(file);
        this.setClasspath(files);
    }

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

    @SuppressWarnings("unchecked")
    @Override
    void arguments(Provider<String>... arguments);
}

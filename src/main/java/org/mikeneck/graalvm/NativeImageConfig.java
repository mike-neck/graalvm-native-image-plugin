package org.mikeneck.graalvm;

import java.io.File;
import java.nio.file.Path;
import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;

public interface NativeImageConfig {

    void setGraalVmHome(String graalVmHome);

    /**
     * set JarTask
     * @param jarTask - jarTask which builds application.
     * @deprecated use {@link #setClasspath} instead.
     */
    @Deprecated
    void setJarTask(Jar jarTask);

    void setClasspath(FileCollection files);

    void setClasspath(Jar jarTask);

    void setMainClass(String mainClass);

    void setExecutableName(String name);

    void setRuntimeClasspath(Configuration configuration);

    void setOutputDirectory(File directory);

    void setOutputDirectory(Path directory);

    void setOutputDirectory(String directory);

    void setOutputDirectory(Provider<Directory> directory);

    void arguments(String... arguments);

    @SuppressWarnings("unchecked")
    void arguments(Provider<String>... arguments);


    /**
     * Represents native-image command options. You can configure command options via this interface.
     */
    interface ArgumentsConfig {
        /**
         * Passes an option to native-image one by one. 
         * @param argument - native-image option argument.
         */
        void add(String argument);

        /**
         * Passes an option to native-image one by one. 
         * @param argument - native-image option argument.
         */
        void add(Provider<String> argument);
    }

    @SuppressWarnings("unchecked")
    default void arguments(@NotNull Action<ArgumentsConfig> config) {
        NativeImageConfig thisConfig = this;
        ArgumentsConfig argumentsConfig = new ArgumentsConfig() {
            @Override
            public void add(String argument) {
                thisConfig.arguments(argument);
            }

            @Override
            public void add(Provider<String> argument) {
                thisConfig.arguments(argument);
            }
        };
        config.execute(argumentsConfig);
    }
}

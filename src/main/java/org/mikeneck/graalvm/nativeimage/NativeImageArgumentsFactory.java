package org.mikeneck.graalvm.nativeimage;

import java.util.ServiceLoader;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.jetbrains.annotations.NotNull;

public interface NativeImageArgumentsFactory {

    @NotNull
    static NativeImageArgumentsFactory getInstance() {
        String osName = System.getProperty("os.name");
        OperatingSystem os = OperatingSystem.byName(osName);
        ServiceLoader<NativeImageArgumentsFactory> serviceLoader = ServiceLoader.load(NativeImageArgumentsFactory.class);
        for (NativeImageArgumentsFactory nativeImageArgumentsFactory : serviceLoader) {
            if (nativeImageArgumentsFactory.supports(os)) {
                return nativeImageArgumentsFactory;
            }
        }
        throw new IllegalStateException(String.format("Unsupported os: %s", osName));
    }

    boolean supports(@NotNull OperatingSystem os);

    @NotNull
    NativeImageArguments create(
            @NotNull Property<Configuration> runtimeClasspath,
            @NotNull Property<String> mainClass,
            @NotNull ConfigurableFileCollection jarFile,
            @NotNull DirectoryProperty outputDirectory,
            @NotNull Property<String> executableName,
            @NotNull ListProperty<String> additionalArguments, @NotNull ConfigurationFiles configurationFiles);
}

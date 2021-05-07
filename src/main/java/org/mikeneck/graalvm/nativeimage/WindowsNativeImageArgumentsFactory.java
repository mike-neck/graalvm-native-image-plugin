package org.mikeneck.graalvm.nativeimage;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.jetbrains.annotations.NotNull;

public class WindowsNativeImageArgumentsFactory implements NativeImageArgumentsFactory {

  @Override
  public boolean supports(@NotNull OperatingSystem os) {
    return os == OperatingSystem.WINDOWS;
  }

  @Override
  public @NotNull NativeImageArguments create(
      @NotNull DirectoryProperty buildDirectory,
      @NotNull Property<Configuration> runtimeClasspath,
      @NotNull Property<String> mainClass,
      @NotNull ConfigurableFileCollection jarFile,
      @NotNull DirectoryProperty outputDirectory,
      @NotNull Property<String> executableName,
      @NotNull ListProperty<String> additionalArguments,
      @NotNull ConfigurationFiles configurationFiles,
      @NotNull RegularFileProperty argumentsFile) {
    UnixLikeOsArguments delegate =
        new UnixLikeOsArguments(
            buildDirectory,
            runtimeClasspath,
            mainClass,
            jarFile,
            outputDirectory,
            executableName,
            additionalArguments,
            configurationFiles,
            argumentsFile);
    return new WindowsNativeImageArguments(delegate);
  }
}

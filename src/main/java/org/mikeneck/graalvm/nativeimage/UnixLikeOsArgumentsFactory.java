package org.mikeneck.graalvm.nativeimage;

import java.util.Arrays;
import java.util.Collection;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.jetbrains.annotations.NotNull;

public class UnixLikeOsArgumentsFactory implements NativeImageArgumentsFactory {

  private static final Collection<OperatingSystem> SUPPORTING_OS =
      Arrays.asList(OperatingSystem.LINUX, OperatingSystem.MACOSX);

  @Override
  public boolean supports(@NotNull OperatingSystem os) {
    return SUPPORTING_OS.contains(os);
  }

  @Override
  public @NotNull NativeImageArguments create(
      @NotNull Property<Configuration> runtimeClasspath,
      @NotNull Property<String> mainClass,
      @NotNull Property<BuildTypeOption> buildTypeOption,
      @NotNull ConfigurableFileCollection jarFile,
      @NotNull DirectoryProperty outputDirectory,
      @NotNull Property<String> executableName,
      @NotNull ListProperty<String> additionalArguments,
      @NotNull ConfigurationFiles configurationFiles) {
    return new UnixLikeOsArguments(
        runtimeClasspath,
        mainClass,
        buildTypeOption,
        jarFile,
        outputDirectory,
        executableName,
        additionalArguments,
        configurationFiles);
  }
}

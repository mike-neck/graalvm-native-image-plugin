package org.mikeneck.graalvm.nativeimage;

import java.util.ServiceLoader;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.DefaultNativeImageTask;

public interface NativeImageArgumentsFactory {

  @NotNull
  static NativeImageArgumentsFactory getInstance() {
    String osName = System.getProperty("os.name");
    OperatingSystem os = OperatingSystem.byName(osName);
    ServiceLoader<NativeImageArgumentsFactory> serviceLoader =
        ServiceLoader.load(NativeImageArgumentsFactory.class);
    for (NativeImageArgumentsFactory nativeImageArgumentsFactory : serviceLoader) {
      if (nativeImageArgumentsFactory.supports(os)) {
        return nativeImageArgumentsFactory;
      }
    }
    throw new IllegalStateException(String.format("Unsupported os: %s", osName));
  }

  boolean supports(@NotNull OperatingSystem os);

  @SuppressWarnings("UnstableApiUsage")
  @NotNull
  default NativeImageArguments create(
      @NotNull Project project,
      @NotNull Property<String> mainClass,
      @NotNull Property<Configuration> runtimeClasspath,
      @NotNull ConfigurableFileCollection jarFile) {
    ObjectFactory objectFactory = project.getObjects();
    ProjectLayout projectLayout = project.getLayout();
    Property<String> executableName = objectFactory.property(String.class);
    ListProperty<String> additionalArguments = objectFactory.listProperty(String.class);
    DirectoryProperty outputDirectory =
        objectFactory
            .directoryProperty()
            .value(
                projectLayout
                    .getBuildDirectory()
                    .dir(DefaultNativeImageTask.DEFAULT_OUTPUT_DIRECTORY_NAME));
    return create(
        runtimeClasspath,
        mainClass,
        jarFile,
        outputDirectory,
        executableName,
        additionalArguments,
        new ConfigurationFiles(project));
  }

  @NotNull
  NativeImageArguments create(
      @NotNull Property<Configuration> runtimeClasspath,
      @NotNull Property<String> mainClass,
      @NotNull ConfigurableFileCollection jarFile,
      @NotNull DirectoryProperty outputDirectory,
      @NotNull Property<String> executableName,
      @NotNull ListProperty<String> additionalArguments,
      @NotNull ConfigurationFiles configurationFiles);
}

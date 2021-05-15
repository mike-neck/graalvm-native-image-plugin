package org.mikeneck.graalvm.nativeimage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.NativeImageConfigurationFiles;

public interface NativeImageArguments {

  @Internal
  default List<String> getArguments() {
    BuildTypeOption option = buildType();
    Optional<@NotNull String> sharedLibraryOption = option.sharedLibraryOption();
    Optional<@NotNull String> mainClassName = option.mainClassName();

    List<String> args = new ArrayList<>();
    sharedLibraryOption.ifPresent(args::add);
    args.add("-cp");
    args.add(classpath());
    args.add(outputPath());
    executableName().ifPresent(args::add);
    args.addAll(additionalArguments());
    mainClassName.ifPresent(args::add);
    return Collections.unmodifiableList(args);
  }

  @NotNull
  String classpath();

  @NotNull
  String outputPath();

  @NotNull
  Optional<String> executableName();

  @NotNull
  List<String> additionalArguments();

  void setRuntimeClasspath(@NotNull Provider<Configuration> runtimeClasspath);

  @NotNull
  String mainClass();

  @Deprecated
  void setMainClass(@NotNull Provider<String> mainClass);

  @NotNull
  BuildTypeOption buildType();

  void setBuildType(BuildTypeOption buildTypeOption);

  void addClasspath(@NotNull File jarFile);

  void addClasspath(@NotNull Provider<File> jarFile);

  void addClasspath(@NotNull FileCollection files);

  void addClasspath(@NotNull Jar jar);

  void setClasspath(@NotNull File jarFile);

  void setClasspath(@NotNull Provider<File> jarFile);

  void setClasspath(@NotNull FileCollection files);

  void setClasspath(@NotNull Jar jar);

  @NotNull
  @OutputDirectory
  DirectoryProperty getOutputDirectory();

  void setOutputDirectory(@NotNull Provider<Directory> outputDirectory);

  void setExecutableName(@NotNull Provider<String> executableName);

  void addArguments(@NotNull Provider<Iterable<String>> arguments);

  void configureConfigFiles(@NotNull Action<NativeImageConfigurationFiles> configuration);
}

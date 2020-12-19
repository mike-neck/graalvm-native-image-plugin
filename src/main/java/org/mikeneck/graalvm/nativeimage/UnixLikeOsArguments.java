package org.mikeneck.graalvm.nativeimage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.NativeImageConfigurationFiles;
import org.slf4j.LoggerFactory;

class UnixLikeOsArguments implements NativeImageArguments {

  @NotNull private final Property<Configuration> runtimeClasspath;
  @NotNull private final Property<String> mainClass;
  @NotNull private final ConfigurableFileCollection jarFile;
  @NotNull private final DirectoryProperty outputDirectory;
  @NotNull private final Property<String> executableName;
  @NotNull private final ListProperty<String> additionalArguments;
  @NotNull private final ConfigurationFiles configurationFiles;

  UnixLikeOsArguments(
      @NotNull Property<Configuration> runtimeClasspath,
      @NotNull Property<String> mainClass,
      @NotNull ConfigurableFileCollection jarFile,
      @NotNull DirectoryProperty outputDirectory,
      @NotNull Property<String> executableName,
      @NotNull ListProperty<String> additionalArguments,
      @NotNull ConfigurationFiles configurationFiles) {
    this.runtimeClasspath = runtimeClasspath;
    this.mainClass = mainClass;
    this.jarFile = jarFile;
    this.outputDirectory = outputDirectory;
    this.executableName = executableName;
    this.additionalArguments = additionalArguments;
    this.configurationFiles = configurationFiles;
  }

  @NotNull
  @Override
  public String classpath() {
    List<File> paths = new ArrayList<>(runtimeClasspath());
    jarFile.forEach(paths::add);
    return paths.stream()
        .map(File::getAbsolutePath)
        .collect(Collectors.joining(File.pathSeparator));
  }

  @NotNull
  private Collection<File> runtimeClasspath() {
    return runtimeClasspath.map(Configuration::getFiles).getOrElse(Collections.emptySet());
  }

  @NotNull
  @Override
  public String outputPath() {
    return String.format(
        "-H:Path=%s", outputDirectory.getAsFile().map(File::getAbsolutePath).get());
  }

  @NotNull
  @Override
  public Optional<String> executableName() {
    if (executableName.isPresent()) {
      return Optional.of(String.format("-H:Name=%s", executableName.get()));
    }
    return Optional.empty();
  }

  @NotNull
  @Nested
  public ConfigurationFiles getConfigurationFiles() {
    return configurationFiles;
  }

  @NotNull
  @Override
  public List<String> additionalArguments() {
    List<String> nativeImageConfigOptions = configurationFiles.getArguments();
    List<String> arguments = new ArrayList<>(nativeImageConfigOptions);
    if (additionalArguments.isPresent()) {
      arguments.addAll(additionalArguments.get());
    }
    return Collections.unmodifiableList(arguments);
  }

  @Override
  public @NotNull String mainClass() {
    return mainClass.get();
  }

  @NotNull
  @InputFiles
  public Provider<Configuration> getRuntimeClasspath() {
    return runtimeClasspath;
  }

  @Override
  public void setRuntimeClasspath(@NotNull Provider<Configuration> runtimeClasspath) {
    this.runtimeClasspath.set(runtimeClasspath);
  }

  @NotNull
  @Input
  public Provider<String> getMainClass() {
    return mainClass;
  }

  @Override
  public void setMainClass(@NotNull Provider<String> mainClass) {
    this.mainClass.set(mainClass);
  }

  @InputFiles
  public @NotNull Iterable<File> getJarFiles() {
    LoggerFactory.getLogger(NativeImageArguments.class)
        .info("jar-file: {}/build: {}", jarFile, jarFile.getBuiltBy());
    return jarFile;
  }

  @Override
  public void addClasspath(@NotNull File jarFile) {
    this.jarFile.from(jarFile);
  }

  @Override
  public void addClasspath(@NotNull Provider<File> jarFile) {
    this.jarFile.from(jarFile);
  }

  @Override
  public void addClasspath(@NotNull FileCollection files) {
    this.jarFile.from(files);
  }

  @Override
  public void addClasspath(@NotNull Jar jar) {
    jarFile.builtBy(jar);
    jarFile.from(jar);
  }

  @Override
  public void setClasspath(@NotNull File jarFile) {
    this.jarFile.setFrom(jarFile);
  }

  @Override
  public void setClasspath(@NotNull Provider<File> jarFile) {
    this.jarFile.setFrom(jarFile);
  }

  @Override
  public void setClasspath(@NotNull FileCollection files) {
    this.jarFile.setFrom(files);
  }

  @Override
  public void setClasspath(@NotNull Jar jar) {
    this.jarFile.setBuiltBy(Collections.singleton(jar));
    this.jarFile.setFrom(jar);
  }

  @Override
  @NotNull
  @OutputDirectory
  public DirectoryProperty getOutputDirectory() {
    return outputDirectory;
  }

  @Override
  public void setOutputDirectory(@NotNull Provider<Directory> outputDirectory) {
    this.outputDirectory.set(outputDirectory);
  }

  @NotNull
  @Input
  public Provider<String> getExecutableName() {
    return executableName;
  }

  @Override
  public void setExecutableName(@NotNull Provider<String> executableName) {
    this.executableName.set(executableName);
  }

  @NotNull
  @Input
  public ListProperty<String> getAdditionalArguments() {
    return additionalArguments;
  }

  @Override
  public void addArguments(@NotNull Provider<Iterable<String>> arguments) {
    this.additionalArguments.addAll(arguments);
  }

  @Override
  public void configureConfigFiles(@NotNull Action<NativeImageConfigurationFiles> configuration) {
    configuration.execute(configurationFiles);
  }
}

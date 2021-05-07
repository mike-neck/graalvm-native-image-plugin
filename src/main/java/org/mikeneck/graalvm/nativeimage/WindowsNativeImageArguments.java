package org.mikeneck.graalvm.nativeimage;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.NativeImageArgumentsConfig;
import org.mikeneck.graalvm.NativeImageConfigurationFiles;

class WindowsNativeImageArguments implements NativeImageArguments {

  private static final char DOUBLE_QUOT = '"';

  private final NativeImageArguments delegate;

  WindowsNativeImageArguments(NativeImageArguments delegate) {
    this.delegate = delegate;
  }

  @NotNull
  private String wrapValue(String value) {
    return String.format("%s%s%s", DOUBLE_QUOT, value, DOUBLE_QUOT);
  }

  @NotNull
  @Override
  public String classpath() {
    return wrapValue(delegate.classpath());
  }

  @NotNull
  @Override
  public String outputPath() {
    return wrapValue(delegate.outputPath());
  }

  @NotNull
  @Override
  public Optional<String> executableName() {
    return delegate.executableName().map(this::wrapValue);
  }

  @NotNull
  @Override
  public List<String> additionalArguments() {
    return delegate.additionalArguments().stream()
        .map(this::wrapValue)
        .collect(Collectors.toList());
  }

  @NotNull
  @Override
  public String mainClass() {
    return wrapValue(delegate.mainClass());
  }

  @Override
  public @NotNull RegularFileProperty argumentsFile() {
    return delegate.argumentsFile();
  }

  @Override
  public void setRuntimeClasspath(@NotNull Provider<Configuration> runtimeClasspath) {
    delegate.setRuntimeClasspath(runtimeClasspath);
  }

  @Override
  public void setMainClass(@NotNull Provider<String> mainClass) {
    delegate.setMainClass(mainClass);
  }

  @Override
  public void addClasspath(@NotNull File jarFile) {
    delegate.addClasspath(jarFile);
  }

  @Override
  public void addClasspath(@NotNull Provider<File> jarFile) {
    delegate.addClasspath(jarFile);
  }

  @Override
  public void addClasspath(@NotNull FileCollection files) {
    delegate.addClasspath(files);
  }

  @Override
  public void addClasspath(@NotNull Jar jar) {
    delegate.addClasspath(jar);
  }

  @Override
  public void setClasspath(@NotNull File jarFile) {
    delegate.setClasspath(jarFile);
  }

  @Override
  public void setClasspath(@NotNull Provider<File> jarFile) {
    delegate.setClasspath(jarFile);
  }

  @Override
  public void setClasspath(@NotNull FileCollection files) {
    delegate.setClasspath(files);
  }

  @Override
  public void setClasspath(@NotNull Jar jar) {
    delegate.setClasspath(jar);
  }

  @Override
  public @NotNull DirectoryProperty getOutputDirectory() {
    return delegate.getOutputDirectory();
  }

  @Override
  public void setOutputDirectory(@NotNull Provider<Directory> outputDirectory) {
    delegate.setOutputDirectory(outputDirectory);
  }

  @Override
  public void setExecutableName(@NotNull Provider<String> executableName) {
    delegate.setExecutableName(executableName);
  }

  @Override
  public void addArguments(@NotNull String... arguments) {
    delegate.addArguments(arguments);
  }

  @SafeVarargs
  @Override
  public final void addArguments(@NotNull Provider<String>... arguments) {
    delegate.addArguments(arguments);
  }

  @Override
  public void addArguments(@NotNull Provider<Iterable<String>> arguments) {
    delegate.addArguments(arguments);
  }

  @Override
  public void addArguments(ListProperty<String> listProperty) {
    delegate.addArguments(listProperty);
  }

  @Override
  public void applyArgumentsConfig(Action<? super NativeImageArgumentsConfig> config) {
    delegate.applyArgumentsConfig(config);
  }

  @Override
  public void configureConfigFiles(@NotNull Action<NativeImageConfigurationFiles> configuration) {
    delegate.configureConfigFiles(configuration);
  }

  @NotNull
  @Nested
  public NativeImageArguments getDelegate() {
    return delegate;
  }
}

package org.mikeneck.graalvm.nativeimage;

import java.io.File;
import java.nio.file.Path;
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
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.NativeImageArgumentsConfig;
import org.mikeneck.graalvm.NativeImageConfigurationFiles;
import org.slf4j.LoggerFactory;

class UnixLikeOsArguments implements NativeImageArguments {

  private final @NotNull DirectoryProperty buildDirectory;
  @NotNull private final Property<Configuration> runtimeClasspath;
  @NotNull private final Property<String> mainClass;
  @NotNull private final ConfigurableFileCollection jarFile;
  @NotNull private final DirectoryProperty outputDirectory;
  @NotNull private final Property<String> executableName;
  @NotNull private final ListProperty<String> additionalArguments;
  @NotNull private final ConfigurationFiles configurationFiles;
  @NotNull private final RegularFileProperty argumentsFile;

  UnixLikeOsArguments(
      @NotNull DirectoryProperty buildDirectory,
      @NotNull Property<Configuration> runtimeClasspath,
      @NotNull Property<String> mainClass,
      @NotNull ConfigurableFileCollection jarFile,
      @NotNull DirectoryProperty outputDirectory,
      @NotNull Property<String> executableName,
      @NotNull ListProperty<String> additionalArguments,
      @NotNull ConfigurationFiles configurationFiles,
      @NotNull RegularFileProperty argumentsFile) {
    this.buildDirectory = buildDirectory;
    this.runtimeClasspath = runtimeClasspath;
    this.mainClass = mainClass;
    this.jarFile = jarFile;
    this.outputDirectory = outputDirectory;
    this.executableName = executableName;
    this.additionalArguments = additionalArguments;
    this.configurationFiles = configurationFiles;
    this.argumentsFile = argumentsFile;
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
    if (!additionalArguments.isPresent()) {
      return nativeImageConfigOptions;
    }

    List<String> arguments = new ArrayList<>(nativeImageConfigOptions);
    List<String> list = additionalArguments.get();
    for (String arg : list) {
      if (!arg.isEmpty()) {
        arguments.add(arg);
      }
    }
    return Collections.unmodifiableList(arguments);
  }

  @Override
  public @NotNull String mainClass() {
    return mainClass.get();
  }

  @NotNull
  @Override
  public RegularFileProperty argumentsFile() {
    return argumentsFile;
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
  public void addArguments(@NotNull String... arguments) {
    for (String arg : arguments) {
      this.additionalArguments.add(arg);
    }
  }

  @SafeVarargs
  @Override
  public final void addArguments(@NotNull Provider<String>... arguments) {
    for (Provider<String> arg : arguments) {
      this.additionalArguments.add(arg);
    }
  }

  @Override
  public void addArguments(@NotNull Provider<Iterable<String>> arguments) {
    this.additionalArguments.addAll(arguments);
  }

  @Override
  public void addArguments(ListProperty<String> listProperty) {
    this.additionalArguments.addAll(listProperty);
  }

  @Override
  public void setArgumentsFile(@NotNull File file) {
    this.argumentsFile.set(file);
  }

  @Override
  public void setArgumentsFile(@NotNull Path file) {
    this.argumentsFile.set(file.toFile());
  }

  @Override
  public void setArgumentsFile(@NotNull RegularFile file) {
    this.argumentsFile.set(file);
  }

  @Override
  public void applyArgumentsConfig(Action<? super NativeImageArgumentsConfig> config) {
    NativeImageArgumentsConfig nativeImageArgumentsConfig = new NativeImageArgumentsConfigImpl();
    config.execute(nativeImageArgumentsConfig);
  }

  @Override
  public void configureConfigFiles(@NotNull Action<NativeImageConfigurationFiles> configuration) {
    configuration.execute(configurationFiles);
  }

  private class NativeImageArgumentsConfigImpl implements NativeImageArgumentsConfig {
    @Override
    public void preferByFile() {
      Provider<RegularFile> file =
          buildDirectory.map(
              directory ->
                  directory.dir("tmp").dir("native-image-arguments").file("arguments.txt"));
      preferByFile(file);
    }

    @Override
    public void preferByFile(@NotNull File file) {
      argumentsFile.set(file);
    }

    @Override
    public void preferByFile(@NotNull Path file) {
      argumentsFile.set(file.toAbsolutePath().toFile());
    }

    @Override
    public void preferByFile(@NotNull RegularFile file) {
      argumentsFile.set(file);
    }

    @Override
    public void preferByFile(@NotNull Provider<? extends RegularFile> file) {
      argumentsFile.set(file);
    }

    @Override
    public void add(String argument) {
      additionalArguments.add(argument);
    }

    @Override
    public void add(Provider<String> argument) {
      additionalArguments.add(argument);
    }
  }
}

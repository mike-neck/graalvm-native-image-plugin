package org.mikeneck.graalvm;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.nativeimage.ConfigurationFiles;
import org.mikeneck.graalvm.nativeimage.NativeImageArguments;
import org.mikeneck.graalvm.nativeimage.NativeImageArgumentsFactory;
import org.mikeneck.graalvm.nativeimage.options.DefaultOptions;
import org.mikeneck.graalvm.nativeimage.options.Options;
import org.slf4j.LoggerFactory;

public class DefaultNativeImageTask extends DefaultTask implements NativeImageTask {

  public static final String DEFAULT_OUTPUT_DIRECTORY_NAME = "native-image";

  @NotNull private final Property<GraalVmHome> graalVmHome;

  @NotNull private final NativeImageArguments nativeImageArguments;

  @SuppressWarnings("UnstableApiUsage")
  @Inject
  public DefaultNativeImageTask(
      @NotNull Project project,
      @NotNull Property<GraalVmHome> graalVmHome,
      @NotNull Property<String> mainClass,
      @NotNull Property<Configuration> runtimeClasspath,
      @NotNull ConfigurableFileCollection jarFile) {
    ObjectFactory objectFactory = project.getObjects();
    ProjectLayout projectLayout = project.getLayout();
    this.graalVmHome = graalVmHome;
    @NotNull Property<String> executableName = objectFactory.property(String.class);
    @NotNull ListProperty<String> additionalArguments = objectFactory.listProperty(String.class);
    @NotNull
    DirectoryProperty outputDirectory =
        objectFactory
            .directoryProperty()
            .value(projectLayout.getBuildDirectory().dir(DEFAULT_OUTPUT_DIRECTORY_NAME));
    NativeImageArgumentsFactory nativeImageArgumentsFactory =
        NativeImageArgumentsFactory.getInstance();
    this.nativeImageArguments =
        nativeImageArgumentsFactory.create(
            runtimeClasspath,
            mainClass,
            jarFile,
            outputDirectory,
            executableName,
            additionalArguments,
            new ConfigurationFiles(project));
  }

  @TaskAction
  public void createNativeImage() {
    createOutputDirectoryIfNotExisting();
    Path nativeImageCommand = nativeImageCommand();
    getProject()
        .exec(
            execSpec -> {
              getLogger().info("run native-image binary.");
              execSpec.setExecutable(nativeImageCommand);
              execSpec.args(arguments());
            });
  }

  private Path nativeImageCommand() {
    GraalVmHome graalVmHome = graalVmHome();
    Optional<Path> nativeImage = graalVmHome.nativeImage();
    if (!nativeImage.isPresent()) {
      getLogger().warn("native-image not found in graalVmHome({})", graalVmHome);
      throw new InvalidUserDataException(
          "native-image not found in graalVmHome(" + graalVmHome + ")");
    }
    return nativeImage.get();
  }

  private GraalVmHome graalVmHome() {
    return graalVmHome.get();
  }

  @NotNull
  @Internal
  public Property<GraalVmHome> getGraalVmHome() {
    return graalVmHome;
  }

  @Override
  @NotNull
  @Internal
  public Provider<GraalVmVersion> getGraalVmVersion() {
    return graalVmHome.map(GraalVmHome::graalVmVersion);
  }

  @Override
  @NotNull
  @Internal
  public Options getOptions() {
    return new DefaultOptions(getGraalVmVersion());
  }

  public File outputDirectory() {
    return nativeImageArguments.getOutputDirectory().getAsFile().get();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void createOutputDirectoryIfNotExisting() {
    File outputDir = outputDirectory();
    getLogger().info("create output directory if not exists: {}", outputDir);
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }
  }

  private List<String> arguments() {
    return getArguments().getOrElse(Collections.emptyList());
  }

  @Input
  public ListProperty<String> getArguments() {
    Project project = getProject();
    ObjectFactory objects = project.getObjects();
    ListProperty<String> listProperty = objects.listProperty(String.class);
    listProperty.set(project.provider(nativeImageArguments::getArguments));
    return listProperty;
  }

  @Override
  @NotNull
  @Nested
  public NativeImageArguments getNativeImageArguments() {
    return nativeImageArguments;
  }

  @Override
  public void setGraalVmHome(String graalVmHome) {
    this.graalVmHome.set(getProject().provider(() -> new GraalVmHome(Paths.get(graalVmHome))));
  }

  @Deprecated
  @Override
  public void setJarTask(Jar jarTask) {
    LoggerFactory.getLogger(NativeImageTask.class)
        .warn("jarTask is deprecated. Please use setClasspath(Jar) instead.");
    nativeImageArguments.setClasspath(jarTask);
  }

  @Override
  public void setClasspath(FileCollection files) {
    nativeImageArguments.setClasspath(files);
  }

  @Override
  public void setClasspath(Jar jarTask) {
    nativeImageArguments.setClasspath(jarTask);
  }

  @Override
  public void setMainClass(String mainClass) {
    Project project = getProject();
    nativeImageArguments.setMainClass(project.provider(() -> mainClass));
  }

  @Override
  public void setExecutableName(String name) {
    nativeImageArguments.setExecutableName(getProject().provider(() -> name));
  }

  @Override
  public void setRuntimeClasspath(Configuration configuration) {
    Project project = getProject();
    nativeImageArguments.setRuntimeClasspath(project.provider(() -> configuration));
  }

  @Override
  public void setOutputDirectory(Provider<Directory> directory) {
    nativeImageArguments.setOutputDirectory(directory);
  }

  @Override
  public void withConfigFiles(@NotNull Action<NativeImageConfigurationFiles> configuration) {
    nativeImageArguments.configureConfigFiles(configuration);
  }

  @Override
  public void arguments(String... arguments) {
    Project project = getProject();
    ListProperty<String> listProperty = project.getObjects().listProperty(String.class);
    for (String argument : arguments) {
      if (!argument.isEmpty()) {
        listProperty.add(argument);
      }
    }
    nativeImageArguments.addArguments(listProperty);
  }

  @SafeVarargs
  @Override
  public final void arguments(Provider<String>... arguments) {
    Project project = getProject();
    ListProperty<String> listProperty = project.getObjects().listProperty(String.class);
    for (Provider<String> argument : arguments) {
      listProperty.add(argument);
    }
    nativeImageArguments.addArguments(listProperty);
  }

  @Override
  public void arguments(@NotNull Action<ArgumentsConfig> config) {
    NativeImageConfig thisConfig = this;
    ArgumentsConfig argumentsConfig =
        new ArgumentsConfig() {
          @Override
          public void add(String argument) {
            thisConfig.arguments(argument);
          }

          @SuppressWarnings("unchecked")
          @Override
          public void add(Provider<String> argument) {
            thisConfig.arguments(argument);
          }
        };
    config.execute(argumentsConfig);
  }
}

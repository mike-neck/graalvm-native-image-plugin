package org.mikeneck.graalvm;

import groovy.lang.Closure;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileCollection;
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
import org.mikeneck.graalvm.nativeimage.BuildTypeOption;
import org.mikeneck.graalvm.nativeimage.NativeImageArguments;
import org.mikeneck.graalvm.nativeimage.options.DefaultOptions;
import org.mikeneck.graalvm.nativeimage.options.Options;
import org.mikeneck.graalvm.nativeimage.options.type.BuildExecutable;
import org.mikeneck.graalvm.nativeimage.options.type.BuildSharedLibrary;
import org.slf4j.LoggerFactory;

public class DefaultNativeImageTask extends DefaultTask implements NativeImageTask {

  public static final String DEFAULT_OUTPUT_DIRECTORY_NAME = "native-image";

  @NotNull private final Property<GraalVmHome> graalVmHome;

  @NotNull private final NativeImageArguments nativeImageArguments;

  @Inject
  public DefaultNativeImageTask(
      @NotNull Property<GraalVmHome> graalVmHome,
      @NotNull NativeImageArguments nativeImageArguments) {
    this.graalVmHome = graalVmHome;
    this.nativeImageArguments = nativeImageArguments;
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
    Provider<String> mainClassProvider = getProject().provider(() -> mainClass);
    nativeImageArguments.setMainClass(mainClassProvider);
    nativeImageArguments.setBuildType(new BuildExecutable(mainClass));
  }

  @Override
  public void buildType(@NotNull BuildTypeConfiguration buildTypeConfiguration) {
    BuildTypeSelector selector =
        new BuildTypeSelector() {
          @Override
          public @NotNull BuildType getSharedLibrary() {
            return BuildSharedLibrary.INSTANCE;
          }

          @Override
          public @NotNull BuildType executable(
              @NotNull Action<BuildExecutableOption> executableOptionConfig) {
            String[] mainClassName = new String[1];
            BuildExecutableOption option = mainClass -> mainClassName[0] = mainClass;
            executableOptionConfig.execute(option);
            if (mainClassName[0] == null) {
              throw new IllegalArgumentException(
                  "Main class name is required for buildType=executable");
            }
            return new BuildExecutable(mainClassName[0]);
          }

          @Override
          public @NotNull BuildType executable(@NotNull Closure<Void> executableOptionConfig) {
            String[] mainClassName = new String[1];
            BuildExecutableOption option = mainClass -> mainClassName[0] = mainClass;
            executableOptionConfig.setDelegate(option);
            executableOptionConfig.call(option);
            if (mainClassName[0] == null) {
              throw new IllegalArgumentException(
                  "Main class name is required for buildType=executable");
            }
            return new BuildExecutable(mainClassName[0]);
          }
        };
    BuildTypeOption buildType = (BuildTypeOption) buildTypeConfiguration.select(selector);
    nativeImageArguments.setBuildType(buildType);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public void buildType(@NotNull Closure<@NotNull BuildType> buildTypeConfiguration) {
    buildType(buildTypeConfiguration::call);
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
    nativeImageArguments.addArguments(
        project.provider(
            () ->
                Arrays.stream(arguments).filter(it -> !it.isEmpty()).collect(Collectors.toList())));
  }

  @SafeVarargs
  @Override
  public final void arguments(Provider<String>... arguments) {
    Project project = getProject();
    ListProperty<String> listProperty = project.getObjects().listProperty(String.class);
    for (Provider<String> argument : arguments) {
      listProperty.add(argument);
    }
    nativeImageArguments.addArguments(
        listProperty.map(
            list -> list.stream().filter(it -> !it.isEmpty()).collect(Collectors.toList())));
  }
}

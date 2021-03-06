package org.mikeneck.graalvm;

import java.nio.file.Paths;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.ProviderFactory;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.nativeimage.BuildTypeOption;
import org.mikeneck.graalvm.nativeimage.ConfigurationFiles;
import org.mikeneck.graalvm.nativeimage.NativeImageArguments;
import org.mikeneck.graalvm.nativeimage.NativeImageArgumentsFactory;

public class NativeImageTaskFactory {

  @NotNull private final Project project;
  @NotNull private final Property<GraalVmHome> graalVmHome;
  @NotNull private final Property<Configuration> runtimeClasspath;
  @NotNull private final ConfigurableFileCollection jarFile;
  @NotNull private final Property<String> mainClass;

  @SuppressWarnings("UnstableApiUsage")
  NativeImageTaskFactory(@NotNull Project project) {
    ObjectFactory objectFactory = project.getObjects();
    ProviderFactory providerFactory = project.getProviders();

    this.project = project;
    this.graalVmHome =
        objectFactory
            .property(GraalVmHome.class)
            .convention(
                providerFactory
                    .environmentVariable("JAVA_HOME")
                    .map(Paths::get)
                    .map(GraalVmHome::new));
    this.runtimeClasspath =
        objectFactory
            .property(Configuration.class)
            .convention(
                project.provider(() -> project.getConfigurations().getByName("runtimeClasspath")));
    this.jarFile = objectFactory.fileCollection();
    this.jarFile.builtBy("jar");
    this.jarFile.setFrom(
        project.provider(() -> project.getTasks().getByName("jar").getOutputs().getFiles()));
    this.mainClass = objectFactory.property(String.class);
  }

  @SuppressWarnings("UnstableApiUsage")
  NativeImageTask nativeImageTask(@NotNull Action<NativeImageTask> config) {
    ObjectFactory objectFactory = project.getObjects();
    ProjectLayout projectLayout = project.getLayout();
    Property<BuildTypeOption> buildTypeOption = objectFactory.property(BuildTypeOption.class);
    @NotNull Property<String> executableName = objectFactory.property(String.class);
    @NotNull ListProperty<String> additionalArguments = objectFactory.listProperty(String.class);
    @NotNull
    DirectoryProperty outputDirectory =
        objectFactory
            .directoryProperty()
            .value(
                projectLayout
                    .getBuildDirectory()
                    .dir(DefaultNativeImageTask.DEFAULT_OUTPUT_DIRECTORY_NAME));
    NativeImageArgumentsFactory nativeImageArgumentsFactory =
        NativeImageArgumentsFactory.getInstance();
    NativeImageArguments nativeImageArguments =
        nativeImageArgumentsFactory.create(
            runtimeClasspath,
            mainClass,
            buildTypeOption,
            jarFile,
            outputDirectory,
            executableName,
            additionalArguments,
            new ConfigurationFiles(project));
    NativeImageTask nativeImage =
        project
            .getTasks()
            .create("nativeImage", DefaultNativeImageTask.class, graalVmHome, nativeImageArguments);
    config.execute(nativeImage);
    return nativeImage;
  }

  InstallNativeImageTask installNativeImageTask(@NotNull Action<InstallNativeImageTask> config) {
    InstallNativeImageTask installNativeImage =
        project
            .getTasks()
            .create("installNativeImage", DefaultInstallNativeImageTask.class, graalVmHome);
    config.execute(installNativeImage);
    return installNativeImage;
  }

  GenerateNativeImageConfigTask nativeImageConfigFilesTask(
      @NotNull Action<GenerateNativeImageConfigTask> config) {
    GenerateNativeImageConfigTask generateNativeImageConfigTask =
        project
            .getTasks()
            .create(
                "generateNativeImageConfig",
                DefaultGenerateNativeImageConfigTask.class,
                project,
                graalVmHome,
                mainClass,
                runtimeClasspath,
                jarFile);
    config.execute(generateNativeImageConfigTask);
    return generateNativeImageConfigTask;
  }

  MergeNativeImageConfigTask mergeNativeImageConfigTask(
      @NotNull Action<MergeNativeImageConfigTask> config) {
    DefaultMergeNativeImageConfigTask mergeNativeImageConfigTask =
        project
            .getTasks()
            .create("mergeNativeImageConfig", DefaultMergeNativeImageConfigTask.class, project);
    config.execute(mergeNativeImageConfigTask);
    return mergeNativeImageConfigTask;
  }
}

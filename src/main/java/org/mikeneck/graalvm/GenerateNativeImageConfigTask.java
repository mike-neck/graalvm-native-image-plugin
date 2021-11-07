package org.mikeneck.graalvm;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.jetbrains.annotations.NotNull;

public interface GenerateNativeImageConfigTask extends ShareEnabledState {

  default void setGraalVmHome(@NotNull String path) {
    setGraalVmHome(getProject().file(path));
  }

  default void setGraalVmHome(@NotNull File path) {
    setGraalVmHome(path.toPath());
  }

  default void setGraalVmHome(@NotNull Path path) {
    Project project = getProject();
    setGraalVmHome(project.provider(() -> new GraalVmHome(path)));
  }

  void setGraalVmHome(@NotNull Provider<GraalVmHome> graalVmHome);

  void setExitOnApplicationError(boolean exitOnApplicationError);

  @Internal
  @NotNull
  Property<GraalVmHome> getGraalVmHome();

  @Internal
  boolean getExitOnApplicationError();

  @Input
  @Optional
  @NotNull
  Provider<String> getMainClass();

  void resumeOnApplicationError();

  @NotNull
  @Nested
  List<? extends JavaExecutionOutput> getJavaExecutions();

  @Deprecated
  @OutputDirectory
  @NotNull
  File getTemporaryDirectory();

  void byRunningApplicationWithoutArguments();

  void byRunningApplication(Action<JavaExecution> argumentsConfiguration);
}

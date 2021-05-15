package org.mikeneck.graalvm;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.jetbrains.annotations.NotNull;

public interface BuildTypeSelector {

  @NotNull
  BuildType getSharedLibrary();

  @NotNull
  BuildType executable(@NotNull Action<BuildExecutableOption> executableOptionConfig);

  @NotNull
  BuildType executable(@NotNull Closure<Void> executableOption);
}

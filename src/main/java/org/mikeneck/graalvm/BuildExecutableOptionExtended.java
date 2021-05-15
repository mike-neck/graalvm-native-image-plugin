package org.mikeneck.graalvm;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface BuildExecutableOptionExtended {

  @NotNull
  BuildType setMain(@NotNull String mainClass);
}

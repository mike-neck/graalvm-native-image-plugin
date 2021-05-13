package org.mikeneck.graalvm;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface BuildTypeConfiguration {

  @NotNull
  BuildType select(@NotNull BuildTypeSelector build);
}

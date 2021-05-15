package org.mikeneck.graalvm;

import org.jetbrains.annotations.NotNull;

public interface BuildExecutableOption {

  void setMain(@NotNull String mainClass);
}

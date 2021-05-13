package org.mikeneck.graalvm;

import org.jetbrains.annotations.NotNull;

public interface BuildExecutableOption {

  void setMainClass(@NotNull String mainClass);
}

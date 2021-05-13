package org.mikeneck.graalvm.nativeimage;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.BuildType;

public interface BuildTypeOption extends BuildType {

  @NotNull
  Optional<@NotNull String> firstOption();

  @NotNull
  Optional<@NotNull String> lastOption();
}

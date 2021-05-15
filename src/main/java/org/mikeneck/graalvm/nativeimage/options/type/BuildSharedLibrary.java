package org.mikeneck.graalvm.nativeimage.options.type;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.nativeimage.BuildTypeOption;

public class BuildSharedLibrary implements BuildTypeOption {

  public static final BuildTypeOption INSTANCE = new BuildSharedLibrary();

  private BuildSharedLibrary() {}

  @Override
  public @NotNull Optional<@NotNull String> sharedLibraryOption() {
    return Optional.of("--shared");
  }

  @Override
  public @NotNull Optional<@NotNull String> mainClassName() {
    return Optional.empty();
  }

  @Override
  public String toString() {
    return "build shared library";
  }
}

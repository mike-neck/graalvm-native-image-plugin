package org.mikeneck.graalvm.nativeimage.options.type;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.nativeimage.BuildTypeOption;

public class BuildExecutable implements BuildTypeOption {

  private final @NotNull String mainClass;

  public BuildExecutable(@NotNull String mainClass) {
    this.mainClass = mainClass;
  }

  @Override
  public String toString() {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder sb = new StringBuilder("BuildExecutable{");
    sb.append("mainClass='").append(mainClass).append('\'');
    sb.append('}');
    return sb.toString();
  }

  @Override
  public @NotNull Optional<String> sharedLibraryOption() {
    return Optional.empty();
  }

  @Override
  public @NotNull Optional<String> mainClassName() {
    return Optional.of(mainClass);
  }
}

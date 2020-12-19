package org.mikeneck.graalvm.config.task;

import java.io.IOException;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Mapper<@NotNull I, @NotNull P> {

  @NotNull
  P work(@NotNull I input) throws IOException;

  @NotNull
  default Optional<P> workOptional(@NotNull I input) {
    try {
      return Optional.of(work(input));
    } catch (@NotNull IOException ignored) {
      return Optional.empty();
    }
  }
}

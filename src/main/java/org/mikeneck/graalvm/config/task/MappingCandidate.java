package org.mikeneck.graalvm.config.task;

import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface MappingCandidate<@NotNull I, @NotNull P> {

  @NotNull
  Optional<@NotNull Outcome<I, P>> examine(@NotNull I input);

  @NotNull
  static <@NotNull I, @NotNull P> MappingCandidate<I, P> empty() {
    return input -> Optional.empty();
  }

  @NotNull
  static <@NotNull I, @NotNull P> MappingCandidate<I, P> present(
      @NotNull Function<@NotNull ? super I, @NotNull ? extends Outcome<I, P>> transform) {
    return input -> Optional.of(transform.apply(input));
  }
}

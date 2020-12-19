package org.mikeneck.graalvm.config.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class MappingCandidatesTest {

  static <@NotNull I, @NotNull P> Outcome<I, P> outcome(
      @NotNull P product,
      @NotNull Predicate<I> errorCase,
      @NotNull Function<? super I, ? extends P> transform) {
    return Outcome.forTest(
        product,
        input -> {
          if (errorCase.test(input))
            throw new IOException(String.format("[%s] not matches", input));
          return transform.apply(input);
        });
  }

  @Test
  void foundFirst() {
    MappingCandidates<String, String> mappingCandidates =
        MappingCandidates.forTest(
            input ->
                Optional.of(
                    outcome(
                        "Foo",
                        (String string) -> string.isEmpty() || string.length() > 3,
                        string -> string.substring(0, 1).toUpperCase() + string.substring(1))),
            input -> Optional.of(Outcome.forTest("3", string -> "" + string.length())));

    Optional<Outcome<String, String>> optional = mappingCandidates.findOut("foo");
    if (!optional.isPresent()) {
      fail("expected outcome to be present");
    }
    Outcome<String, String> outcome = optional.get();
    assertAll(
        () -> assertThat(outcome.detectedProduct()).isEqualTo("Foo"),
        () -> assertThat(outcome.detectedMapper().work("bar")).isEqualTo("Bar"),
        () -> assertThrows(IOException.class, () -> outcome.detectedMapper().work("")));
  }

  @Test
  void foundSecond() {
    MappingCandidates<String, String> mappingCandidates =
        MappingCandidates.forTest(
            MappingCandidate.empty(),
            MappingCandidate.present(
                string -> outcome("3", str -> str.contains("/"), str -> "" + str.length())),
            MappingCandidate.empty());

    Optional<@NotNull Outcome<String, String>> optional = mappingCandidates.findOut("foo");

    if (!optional.isPresent()) {
      fail("expected outcome to be present");
    }
    Outcome<String, String> outcome = optional.get();

    assertAll(
        () -> assertThat(outcome.detectedProduct()).isEqualTo("3"),
        () -> assertThat(outcome.detectedMapper().workOptional("/")).isEmpty(),
        () -> assertThat(outcome.detectedMapper().workOptional("bar-baz")).hasValue("7"));
  }

  @Test
  void allEmpty() {
    MappingCandidates<String, URL> mappingCandidates =
        MappingCandidates.forTest(
            MappingCandidate.empty(), MappingCandidate.empty(), MappingCandidate.empty());

    Optional<@NotNull Outcome<String, URL>> optional = mappingCandidates.findOut("foo");

    assertThat(optional).isEmpty();
  }
}

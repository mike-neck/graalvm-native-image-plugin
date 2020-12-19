package org.mikeneck.graalvm.config.task;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

public interface MappingCandidates<@NotNull I, @NotNull P> extends Iterable<@NotNull MappingCandidate<I, P>> {

    @NotNull
    default Optional<@NotNull Outcome<I, P>> findOut(@NotNull I first) {
        for (@NotNull MappingCandidate<I, P> mappingCandidate : this) {
            Optional<Outcome<I, P>> outcome = mappingCandidate.examine(first);
            if (outcome.isPresent()) {
                return outcome;
            }
        }
        return Optional.empty();
    }

    @SafeVarargs
    @NotNull
    @TestOnly
    static <@NotNull I, @NotNull P> MappingCandidates<I, P> forTest(@NotNull MappingCandidate<I, P>... mappingCandidates) {
        List<@NotNull MappingCandidate<I, P>> list = Arrays.asList(mappingCandidates);
        return list::iterator;
    }
}

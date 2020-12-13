/*
 * Copyright 2020 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mikeneck.graalvm.config.task;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

public interface Candidates<@NotNull I, @NotNull P> extends Iterable<@NotNull Candidate<I, P>> {

    @NotNull
    default Optional<@NotNull Outcome<I, P>> findOut(@NotNull I first) {
        for (@NotNull Candidate<I, P> candidate : this) {
            Optional<Outcome<I, P>> outcome = candidate.examine(first);
            if (outcome.isPresent()) {
                return outcome;
            }
        }
        return Optional.empty();
    }

    @SafeVarargs
    @NotNull
    @TestOnly
    static <@NotNull I, @NotNull P> Candidates<I, P> forTest(@NotNull Candidate<I, P>... candidates) {
        List<@NotNull Candidate<I, P>> list = Arrays.asList(candidates);
        return list::iterator;
    }
}

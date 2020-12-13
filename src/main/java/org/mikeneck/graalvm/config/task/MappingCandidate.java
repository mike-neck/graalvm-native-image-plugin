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

import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface MappingCandidate<@NotNull I, @NotNull P> {

    @NotNull Optional<@NotNull Outcome<I, P>> examine(@NotNull I input);

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
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

import java.io.IOException;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Mapper<@NotNull I, @NotNull P> {

    @NotNull P work(@NotNull I input) throws IOException;

    @NotNull default Optional<P> workOptional(@NotNull I input) {
        try {
            return Optional.of(work(input));
        } catch (@NotNull IOException ignored) {
            return Optional.empty();
        }
    }
}

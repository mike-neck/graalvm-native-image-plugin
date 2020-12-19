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

import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.config.SelectableMergeableConfig;

public interface SelectorMergeConfigFileWorkFactory {

    FileInputsSelection resource(@NotNull String resourceName);

    interface FileInputsSelection {
        MappingCandidateSelection files(Iterable<FileInput> files);
    }

    interface TypeProvider {
        Class<? extends SelectableMergeableConfig<?>> get();

        static <C extends SelectableMergeableConfig<C>> TypeProvider of(Class<C> klass) {
            return () -> klass;
        }
    }

    interface MappingCandidateSelection {
        <C extends SelectableMergeableConfig<C>> FileOutputSelection<C> resourceCandidateTypes(@NotNull TypeProvider... klasses);
    }

    interface FileOutputSelection<C extends SelectableMergeableConfig<C>> {
        MergeConfigFileWork<C> writeTo(@NotNull FileOutput fileOutput);
    }
}

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
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.mikeneck.graalvm.config.ResourceConfig;
import org.mikeneck.graalvm.config.SelectableMergeableConfig;

public class FileInputToResourceConfig$20$3$MappingForTest implements MappingCandidate<FileInput, SelectableMergeableConfig<?>> {

    @Override
    public @NotNull Optional<@NotNull Outcome<FileInput, SelectableMergeableConfig<?>>> examine(@NotNull FileInput input) {
        @SuppressWarnings("Convert2MethodRef")
        Function<FileInput, Optional<ResourceConfig.$20$3>> mapper =
                fileInput ->
                        fileInput.makeUtf8String()
                .flatMap(string -> makeResourceConfig(string));
        return mapper.apply(input)
                .map(resourceConfig ->
                        Outcome.forTest(
                                resourceConfig,
                                file -> mapper.apply(file).orElseThrow(IOException::new)));
    }

    @NotNull
    static FileInput $20$3ResourceConfig(@NotNull String includes, @NotNull String excludes, @NotNull String bundles) {
        String inputText = new StringJoiner("/", "20-3:", "").add(includes).add(excludes).add(bundles).toString();
        return new FileInput() {
            @Override
            public Reader newReader(Charset charset) {
                return new StringReader(inputText);
            }

            @Override
            public String toString() {
                return "$20$3Resource[" + inputText + "]";
            }
        };
    }

    @NotNull
    private static Optional<ResourceConfig.$20$3> makeResourceConfig(@NotNull String string) {
        if (!string.startsWith("20-3:")) {
            return Optional.empty();
        }
        String input = string.replace("20-3:", "");
        String[] parts = input.split("/", 3);
        if (parts.length != 3) {
            return Optional.empty();
        }
        String[] includes = parts[0].split(",");
        String[] excludes = parts[1].split(",");
        String[] bundles = parts[2].split(",");
        ResourceConfig.$20$3 resourceConfig = resourceConfigBuilder
                .includes(includes)
                .excludes(excludes)
                .bundles(bundles);
        return Optional.of(resourceConfig);
    }

    private static final ResourceConfigIncludes resourceConfigBuilder =
            includes -> excludes -> bundles ->
                    new ResourceConfig.$20$3(Arrays.asList(includes), Arrays.asList(excludes), bundles);

    private interface ResourceConfigIncludes {
        @NotNull ResourceConfigExcludes includes(@NotNull String... includes);
    }

    private interface ResourceConfigExcludes {
        @NotNull ResourceConfigBundles excludes(@NotNull String... excludes);
    }

    private interface ResourceConfigBundles {
        @NotNull ResourceConfig.$20$3 bundles(@NotNull String... bundles);
    }
}
